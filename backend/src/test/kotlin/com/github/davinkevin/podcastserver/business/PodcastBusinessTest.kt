package com.github.davinkevin.podcastserver.business

import com.github.davinkevin.podcastserver.entity.Cover
import com.github.davinkevin.podcastserver.entity.Tag
import com.github.davinkevin.podcastserver.service.JdomService
import com.github.davinkevin.podcastserver.service.MimeTypeService
import com.github.davinkevin.podcastserver.service.properties.PodcastServerParameters
import com.nhaarman.mockitokotlin2.*
import com.github.davinkevin.podcastserver.entity.Podcast
import lan.dk.podcastserver.repository.PodcastRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.util.FileSystemUtils
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Created by kevin on 27/07/15 for Podcast Server
 */
@ExtendWith(MockitoExtension::class)
class PodcastBusinessTest {

    val workingFolder = Paths.get("/tmp", "PodcastBusinessTest")

    @Mock lateinit var podcastServerParameters: PodcastServerParameters
    @Mock lateinit var jdomService: JdomService
    @Mock lateinit var podcastRepository: PodcastRepository
    @Mock lateinit var tagBusiness: TagBusiness
    @Mock lateinit var coverBusiness: CoverBusiness
    @Mock lateinit var mimeTypeService: MimeTypeService
    @InjectMocks lateinit var podcastBusiness: PodcastBusiness

    @BeforeEach
    fun beforeEach() {
        FileSystemUtils.deleteRecursively(workingFolder.toFile())
    }

    @Test
    fun should_save() {
        /* Given */
        val podcast = Podcast()
        whenever(podcastRepository.save(any())).then { it.arguments[0] }

        /* When */
        val savedPodcast = podcastBusiness.save(podcast)

        /* Then */
        assertThat(savedPodcast).isSameAs(podcast)
        verify(podcastRepository, times(1)).save(podcast)
    }

    @Test
    fun should_find_one() {
        /* Given */
        val podcastId = UUID.randomUUID()
        val podcast = Podcast().apply { id = podcastId }
        whenever(podcastRepository.findById(any())).then { Optional.of(podcast) }

        /* When */
        val aPodcast = podcastBusiness.findOne(podcastId)

        /* Then */
        assertThat(aPodcast.id).isEqualTo(podcastId)
        assertThat(aPodcast).isSameAs(podcast)
        verify(podcastRepository, times(1)).findById(podcastId)
    }

    @Test
    fun should_throw_exception_if_id_not_found() {
        /* Given */
        val id = UUID.randomUUID()
        whenever(podcastRepository.findById(any())).thenReturn(Optional.empty())

        /* When */
        assertThatThrownBy { podcastBusiness.findOne(id) }

        /* Then */
                .isInstanceOf(RuntimeException::class.java)
                .hasMessage("Podcast $id not found")
    }

    @Test
    fun should_delete() {
        /* Given */
        val podcastId = UUID.randomUUID()

        /* When */
        podcastBusiness.delete(podcastId)

        /* Then */
        verify(podcastRepository, times(1)).deleteById(podcastId)
    }

    @Test
    fun should_delete_by_entity() {
        /* Given */
        val podcast = Podcast()

        /* When */
        podcastBusiness.delete(podcast)

        /* Then */
        verify(podcastRepository, times(1)).delete(podcast)
    }

    @Test
    fun should_reattach_and_save() {
        /* Given */
        val tags = setOf(
                Tag().apply { name = "Tag1" }, Tag().apply { name = "Tag2" }
        )

        val podcast = Podcast().apply { this.tags = tags }

        whenever(tagBusiness.getTagListByName(any())).thenReturn(tags)
        whenever(podcastRepository.save(any())).then { it.arguments[0] }

        /* When */
        val savedPodcast = podcastBusiness.reatachAndSave(podcast)

        /* Then */
        assertThat(savedPodcast.tags).containsAll(tags)
        verify(tagBusiness, times(1)).getTagListByName(tags)
        verify(podcastRepository, times(1)).save(podcast)
    }

    @Test
    fun should_patch_podcast() {
        /* Given */
        val podcastId = UUID.randomUUID()
        val tagSet = setOf(
                Tag().apply { name = "Tag1" },
                Tag().apply { name = "Tag2" }
        )

        val retrievePodcast = Podcast().apply {
            title = "Titi"
            cover = Cover().apply { url = "http://fake.url/image2.png" }
            id = podcastId
        }

        val idCover = UUID.randomUUID()

        val patchPodcast = Podcast().apply {
                id = podcastId
                title = "Toto"
                url = "http://fake.url/podcast.rss"
                type = "RSS"
                cover = Cover().apply { id = idCover; url = "http://fake.url/image.png" }
                description = "Description"
                hasToBeDeleted = true
                tags = tagSet
        }

        Files.createDirectories(workingFolder.resolve(retrievePodcast.title))
        whenever(podcastServerParameters.rootfolder).thenReturn(workingFolder)
        whenever(podcastRepository.findById(patchPodcast.id!!)).thenReturn(Optional.of(retrievePodcast))
        whenever(coverBusiness.hasSameCoverURL(any(), any())).thenReturn(false)
        whenever(coverBusiness.findOne(any())).then {
            Cover().apply { id = it.arguments[0] as UUID; height = 100; width = 100; url = "http://a.pretty.url.com/image.png" }
        }
        whenever(tagBusiness.getTagListByName(any())).then { it.getArgument(0)  }
        whenever(podcastRepository.save(any())).then { it.arguments[0] }

        /* When */
        val updatedPodcast = podcastBusiness.patchUpdate(patchPodcast)

        /* Then */
        assertThat(updatedPodcast.id).isEqualTo(patchPodcast.id)
        assertThat(updatedPodcast.title).isEqualTo(patchPodcast.title)
        assertThat(updatedPodcast.url).isEqualTo(patchPodcast.url)
        assertThat(updatedPodcast.type).isEqualTo(patchPodcast.type)
        assertThat(updatedPodcast.cover).isEqualTo(patchPodcast.cover)
        assertThat(updatedPodcast.description).isEqualTo(patchPodcast.description)
        assertThat(updatedPodcast.hasToBeDeleted).isEqualTo(patchPodcast.hasToBeDeleted)
        assertThat(updatedPodcast.tags).isEqualTo(tagSet)

        assertThat(workingFolder.resolve(patchPodcast.title)).exists()
        assertThat(workingFolder.resolve("Titi")).doesNotExist()

        verify(podcastRepository, times(1)).findById(podcastId)
        verify(coverBusiness, times(1)).hasSameCoverURL(patchPodcast, retrievePodcast)
        verify(coverBusiness, times(1)).findOne(idCover)
        verify(tagBusiness, times(1)).getTagListByName(tagSet)
        verify(podcastRepository, times(1)).save(retrievePodcast)
    }

    @Test
    fun should_get_cover_of() {
        /* Given */
        val podcastId = UUID.randomUUID()
        val coverPath = Paths.get("/")
        val podcast = Podcast().apply {
            url = "http://an/url"
            title = "Foo"
            id = podcastId
        }

        whenever(podcastRepository.findById(podcastId)).thenReturn(Optional.of(podcast))
        whenever(coverBusiness.getCoverPathOf(podcast)).thenReturn(coverPath)

        /* When */
        val path = podcastBusiness.coverOf(podcastId)

        /* Then */
        assertThat(path).isSameAs(coverPath)
        verify(podcastRepository).findById(podcastId)
        verify(coverBusiness).getCoverPathOf(podcast)
    }
}
