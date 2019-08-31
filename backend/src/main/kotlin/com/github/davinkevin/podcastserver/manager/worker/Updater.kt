package com.github.davinkevin.podcastserver.manager.worker

import com.github.davinkevin.podcastserver.service.image.CoverInformation
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.lang.Exception
import java.net.URI
import java.time.ZonedDateTime
import java.util.*

val log = LoggerFactory.getLogger(Updater::class.java)!!
val defaultPodcast = PodcastToUpdate(id = UUID.randomUUID(), url = URI("https://localhost/"), signature = "")
val NO_MODIFICATION = UpdatePodcastInformation(defaultPodcast, setOf(), "")

interface Updater {

    fun blockingFindItems(podcast: PodcastToUpdate): Set<ItemFromUpdate>

    fun blockingSignatureOf(url: URI): String

    fun update(podcast: PodcastToUpdate): Mono<UpdatePodcastInformation> {
        log.info("podcast {} starts update", podcast.url)
        return signatureOf(podcast.url)
                .filter { sign ->  (podcast.signature != sign)
                        .also {
                            if (it) log.debug("podcast {} has new signature {}", podcast.url, sign)
                            else log.debug("podcast {} hasn't change", podcast.url)
                        }
                }
                .flatMap { sign ->
                    findItems(podcast)
                            .collectList()
                            .map { it.toSet() }
                            .doOnNext { log.debug("podcast {} has {} items found", podcast.url, it.size) }
                            .map { items -> UpdatePodcastInformation(podcast, items, sign) }
                }
                .doOnSuccess { log.info("podcast {} ends update", podcast.url) }
                .doOnError { log.error("podcast {} ends with error", podcast.url, it) }
                .onErrorResume { Mono.empty() }
    }

    fun findItems(podcast: PodcastToUpdate): Flux<ItemFromUpdate> = try {
        blockingFindItems(podcast).toFlux()
    } catch (e: Exception) { Flux.error(e) }

    fun signatureOf(url: URI): Mono<String> = try {
        blockingSignatureOf(url).toMono()
    } catch (e: Exception) { Mono.error(e) }

    fun type(): Type

    fun compatibility(url: String?): Int
}


data class UpdatePodcastInformation(val podcast: PodcastToUpdate, val items: Set<ItemFromUpdate>, val newSignature: String)
data class PodcastToUpdate(val id: UUID, val url: URI, val signature: String)
data class ItemFromUpdate(
        val title: String?,
        val pubDate: ZonedDateTime?,
        val length: Long? = null,
        val url: URI,
        val description: String?,
        val cover: CoverFromUpdate?
)
data class CoverFromUpdate(val width: Int, val height: Int, val url: URI)
fun CoverInformation.toCoverFromUpdate() = CoverFromUpdate (
        height = this@toCoverFromUpdate.height,
        width = this@toCoverFromUpdate.width,
        url = this@toCoverFromUpdate.url
)

val defaultItem = ItemFromUpdate(null, ZonedDateTime.now(), null, URI("http://foo.bar"), null, null)
