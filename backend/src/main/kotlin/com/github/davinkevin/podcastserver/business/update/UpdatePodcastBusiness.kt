package com.github.davinkevin.podcastserver.business.update

import arrow.core.Try
import arrow.core.getOrElse
import com.github.davinkevin.podcastserver.business.CoverBusiness
import com.github.davinkevin.podcastserver.entity.Cover
import com.github.davinkevin.podcastserver.entity.Item
import com.github.davinkevin.podcastserver.entity.Podcast
import com.github.davinkevin.podcastserver.entity.Status
import com.github.davinkevin.podcastserver.manager.selector.UpdaterSelector
import com.github.davinkevin.podcastserver.manager.worker.UpdatePodcastInformation
import com.github.davinkevin.podcastserver.manager.worker.Updater
import com.github.davinkevin.podcastserver.manager.worker.Updater.Companion.NO_MODIFICATION
import com.github.davinkevin.podcastserver.service.MessagingTemplate
import com.github.davinkevin.podcastserver.service.properties.PodcastServerParameters
import com.github.davinkevin.podcastserver.utils.k
import lan.dk.podcastserver.repository.ItemRepository
import lan.dk.podcastserver.repository.PodcastRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier
import javax.annotation.PostConstruct
import javax.validation.Validator

@Component
class UpdatePodcastBusiness(
        val podcastRepository: PodcastRepository,
        val itemRepository: ItemRepository,
        val updaterSelector: UpdaterSelector,
        val template: MessagingTemplate,
        val podcastServerParameters: PodcastServerParameters,
        @param:Qualifier("UpdateExecutor") val updateExecutor: ThreadPoolTaskExecutor,
        @param:Qualifier("ManualUpdater") val manualExecutor: ThreadPoolTaskExecutor,
        @param:Qualifier("Validator") val validator: Validator,
        val coverBusiness: CoverBusiness
) {

    val log = LoggerFactory.getLogger(this.javaClass.name)!!

    private var timeUnit = TimeUnit.MINUTES
    private var timeValue: Int = 5

    internal fun setTimeOut(timeValue: Int, timeUnit: TimeUnit) {
        this.timeValue = timeValue
        this.timeUnit = timeUnit
    }

    var lastFullUpdate: ZonedDateTime? = null

    private val _isUpdating = AtomicBoolean(false)
    val isUpdating: Boolean
        get() = _isUpdating.get()

    val updaterActiveCount: Int
        get() = updateExecutor.activeCount + manualExecutor.activeCount

    @Transactional
    fun updatePodcast() {
        updatePodcast(podcastRepository.findByUrlIsNotNull().toJavaSet(), updateExecutor)
        lastFullUpdate = ZonedDateTime.now()
    }

    @Transactional
    fun updatePodcast(id: UUID) {
        val podcast = podcastRepository.findById(id)
                .k()
                .getOrElse{ throw RuntimeException("Podcast with ID $id not found") }

        updatePodcast(setOf(podcast), manualExecutor)
    }

    @Transactional
    fun forceUpdatePodcast(id: UUID) {
        log.info("Launch forced update on podcast with id: $id")
        var podcast = podcastRepository.findById(id)
                .k()
                .getOrElse{ throw RuntimeException("Podcast with ID $id not found") }
        podcast.signature = ""
        podcast = podcastRepository.save(podcast)
        updatePodcast(podcast.id!!)
    }

    private fun updatePodcast(podcasts: Set<Podcast>, selectedExecutor: Executor) {
        changeAndCommunicateUpdate(true)

        log.info("Update launch")
        log.info("About to update {} podcast(s)", podcasts.size)

        podcasts
                .map { supplyAsync(Supplier { updaterSelector.of(it.url).update(it) }, selectedExecutor) }
                .stream()
                .map { wait(it) }
                .filter { it != NO_MODIFICATION }
                .peek { changeAndCommunicateUpdate(true) }
                .map { attachNewItemsToPodcast(it.podcast, it.items, it.p) }
                .flatMap { it.stream() }
                .forEach { coverBusiness.download(it) }

        log.info("End of treatment on {} podcasts", podcasts.size)

        changeAndCommunicateUpdate(false)
    }

    private fun changeAndCommunicateUpdate(isUpdating: Boolean) {
        _isUpdating.set(isUpdating)
        this.template.convertAndSend(WS_TOPIC_UPDATING, _isUpdating.get())
    }

    private fun wait(future: CompletableFuture<UpdatePodcastInformation>): UpdatePodcastInformation =
            Try { future.get(timeValue.toLong(), timeUnit) }
                    .getOrElse {
                        log.error("Error during update", it)
                        future.cancel(true)
                        Updater.NO_MODIFICATION
                    }

    private fun attachNewItemsToPodcast(p: Podcast, items: Set<Item>, filter: (Item) -> Boolean): Set<Item> {

        if (items.isEmpty()) {
            log.info("Reset of signature in order to force the next update: {}", p.title)
            p.signature = ""
            podcastRepository.save(p)
            return setOf()
        }

        val itemsToAdd = items
                .filter(filter)
                .map { it.apply { podcast = p } }
                .filter { validator.validate(it).isEmpty() }
                .map { it.apply { cover = if (cover == Cover.DEFAULT_COVER) podcast?.cover else it.cover } }

        if (itemsToAdd.isEmpty()) {
            return itemsToAdd
                    .toSet()
        }

        itemRepository.saveAll(
                itemsToAdd
                        .map { it.apply { numberOfFail = 0; status = Status.NOT_DOWNLOADED } }
                        .map { p.add(it); it }
        )

        podcastRepository.save(p.lastUpdateToNow())

        return itemsToAdd.toSet()
    }

    fun deleteOldCover() {
        log.info("Deletion of old covers item")

        itemRepository
                .findAllToDelete(podcastServerParameters.limitToKeepCoverOnDisk())
                .flatMap { coverBusiness.getCoverPathOf(it) }
                .forEach { Try { Files.deleteIfExists(it) } }
    }

    @PostConstruct
    fun resetItemWithIncorrectState() {
        log.info("Reset of Started and Paused")

        itemRepository.findByStatus(Status.STARTED, Status.PAUSED)
                .map { it.apply { status = Status.NOT_DOWNLOADED } }
                .forEach { itemRepository.save(it) }
    }

    companion object {
        private const val WS_TOPIC_UPDATING = "/topic/updating"
    }
}
