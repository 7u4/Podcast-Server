package com.github.davinkevin.podcastserver.playlist

import com.github.davinkevin.podcastserver.entity.Status
import com.ninja_squad.dbsetup.DbSetup
import com.ninja_squad.dbsetup.DbSetupTracker
import com.ninja_squad.dbsetup.Operations.insertInto
import com.ninja_squad.dbsetup.destination.DataSourceDestination
import com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf
import lan.dk.podcastserver.repository.DatabaseConfigurationTest.DELETE_ALL
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier
import java.net.URI
import java.time.ZonedDateTime.now
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import java.util.UUID.*
import javax.sql.DataSource
import com.github.davinkevin.podcastserver.playlist.PlaylistRepositoryV2 as PlaylistRepository

/**
 * Created by kevin on 2019-07-06
 */
@JooqTest
@Import(PlaylistRepository::class)
class PlaylistRepositoryV2Test(
    @Autowired val query: DSLContext,
    @Autowired val repository: PlaylistRepository,
    @Autowired val datasourceDestination: DataSourceDestination
) {

    private val dbSetupTracker = DbSetupTracker()
    private val formatter = DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(" ").append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter()

    private val insertPlaylistData = sequenceOf(
            insertInto("COVER")
                    .columns("ID", "URL", "WIDTH", "HEIGHT")
                    .values(fromString("9f050dc4-6a2e-46c3-8276-43098c011e68"), "http://fake.url.com/geekinc/cover.png", 100, 100)
                    .values(fromString("8ea0373e-7af6-4e15-b0fd-9ec4b10822ec"), "http://fake.url.com/appload/cover.png", 100, 100)
                    .build(),
            insertInto("PODCAST")
                    .columns("ID", "TITLE", "URL", "TYPE", "HAS_TO_BE_DELETED")
                    .values(fromString("e9c89e7f-7a8a-43ad-8425-ba2dbad2c561"), "AppLoad", null, "RSS", false)
                    .values(fromString("67b56578-454b-40a5-8d55-5fe1a14673e8"), "Geek Inc HD", "http://fake.url.com/rss", "YOUTUBE", true)
                    .build(),
            insertInto("ITEM")
                    .columns("ID", "TITLE", "URL", "FILE_NAME", "PODCAST_ID", "STATUS", "PUB_DATE", "DOWNLOAD_DATE", "CREATION_DATE", "NUMBER_OF_FAIL", "COVER_ID", "DESCRIPTION")
                    .values(fromString("e3d41c71-37fb-4c23-a207-5fb362fa15bb"), "Appload 1", "http://fakeurl.com/appload.1.mp3", "appload.1.mp3", fromString("e9c89e7f-7a8a-43ad-8425-ba2dbad2c561"), Status.FINISH, now().minusDays(15).format(formatter), now().minusDays(15).format(formatter), null, 0, fromString("8ea0373e-7af6-4e15-b0fd-9ec4b10822ec"), "desc")
                    .values(fromString("817a4626-6fd2-457e-8d27-69ea5acdc828"), "Appload 2", "http://fakeurl.com/appload.2.mp3", "appload.2.mp3", fromString("e9c89e7f-7a8a-43ad-8425-ba2dbad2c561"), null, now().minusDays(30).format(formatter), null, null, 0, fromString("8ea0373e-7af6-4e15-b0fd-9ec4b10822ec"), "desc")
                    .values(fromString("43fb990f-0b5e-413f-920c-6de217f9ecdd"), "Appload 3", "http://fakeurl.com/appload.3.mp3", "appload.3.mp3", fromString("e9c89e7f-7a8a-43ad-8425-ba2dbad2c561"), Status.NOT_DOWNLOADED, now().format(formatter), null, null, 0, fromString("8ea0373e-7af6-4e15-b0fd-9ec4b10822ec"), "desc")
                    .values(fromString("b721a6b6-896a-48fc-b820-28aeafddbb53"), "Geek INC 123", "http://fakeurl.com/geekinc.123.mp3", "geekinc.123.mp3", fromString("67b56578-454b-40a5-8d55-5fe1a14673e8"), Status.DELETED, now().minusYears(1).format(formatter), now().format(formatter), now().minusMonths(2).format(formatter), 0, fromString("9f050dc4-6a2e-46c3-8276-43098c011e68"), "desc")
                    .values(fromString("0a774611-c857-44df-b7e0-5e5af31f7b56"), "Geek INC 124", "http://fakeurl.com/geekinc.124.mp3", "geekinc.124.mp3", fromString("67b56578-454b-40a5-8d55-5fe1a14673e8"), Status.FINISH, now().minusDays(15).format(formatter), now().minusDays(15).format(formatter), now().minusMonths(2).format(formatter), 0, fromString("9f050dc4-6a2e-46c3-8276-43098c011e68"), "desc")
                    .values(fromString("0a774611-c867-44df-b7e0-5e5af31f7b56"), "Geek INC 122", "http://fakeurl.com/geekinc.122.mp3", "geekinc.122.mp3", fromString("67b56578-454b-40a5-8d55-5fe1a14673e8"), Status.FAILED, now().minusDays(1).format(formatter), null, now().minusWeeks(2).format(formatter), 3, fromString("9f050dc4-6a2e-46c3-8276-43098c011e68"), "desc")
                    .values(fromString("0a674611-c867-44df-b7e0-5e5af31f7b56"), "Geek INC 126", "http://fakeurl.com/geekinc.126.mp3", "geekinc.126.mp3", fromString("67b56578-454b-40a5-8d55-5fe1a14673e8"), Status.FAILED, now().minusDays(1).format(formatter), null, now().minusWeeks(1).format(formatter), 7, fromString("9f050dc4-6a2e-46c3-8276-43098c011e68"), "desc")
                    .build(),
            insertInto("TAG")
                    .columns("ID", "NAME")
                    .values(fromString("eb355a23-e030-4966-b75a-b70881a8bd08"), "French Spin")
                    .values(fromString("ad109389-9568-4bdb-ae61-5f26bf6ffdf6"), "Studio Knowhere")
                    .build(),
            insertInto("PODCAST_TAGS")
                    .columns("PODCASTS_ID", "TAGS_ID")
                    .values(fromString("e9c89e7f-7a8a-43ad-8425-ba2dbad2c561"), fromString("eb355a23-e030-4966-b75a-b70881a8bd08"))
                    .values(fromString("67b56578-454b-40a5-8d55-5fe1a14673e8"), fromString("ad109389-9568-4bdb-ae61-5f26bf6ffdf6"))
                    .build(),
            insertInto("WATCH_LIST")
                    .columns("ID", "NAME")
                    .values(fromString("dc024a30-bd02-11e5-a837-0800200c9a66"), "Humour Playlist")
                    .values(fromString("24248480-bd04-11e5-a837-0800200c9a66"), "Conférence Rewind")
                    .values(fromString("9706ba78-2df2-4b37-a573-04367dc6f0ea"), "empty playlist")
                    .build(),
            insertInto("WATCH_LIST_ITEMS")
                    .columns("WATCH_LISTS_ID", "ITEMS_ID")
                    .values(fromString("dc024a30-bd02-11e5-a837-0800200c9a66"), fromString("43fb990f-0b5e-413f-920c-6de217f9ecdd"))
                    .values(fromString("dc024a30-bd02-11e5-a837-0800200c9a66"), fromString("0a774611-c857-44df-b7e0-5e5af31f7b56"))
                    .values(fromString("24248480-bd04-11e5-a837-0800200c9a66"), fromString("0a774611-c857-44df-b7e0-5e5af31f7b56"))
                    .build()
    )

    @BeforeEach
    fun beforeEach() {
        DbSetup(datasourceDestination, sequenceOf(DELETE_ALL, insertPlaylistData)).launch()
    }

    @Nested
    @DisplayName("should find all")
    inner class ShouldFindAll {
        @Test
        fun `with watch lists in results`() {
            /* Given */
            /* When */
            StepVerifier.create(repository.findAll())
                    /* Then */
                    .expectSubscription()
                    .expectNext(Playlist(fromString("24248480-bd04-11e5-a837-0800200c9a66"), "Conférence Rewind"))
                    .expectNext(Playlist(fromString("dc024a30-bd02-11e5-a837-0800200c9a66"), "Humour Playlist"))
                    .expectNext(Playlist(fromString("9706ba78-2df2-4b37-a573-04367dc6f0ea"), "empty playlist"))
                    .verifyComplete()
        }
    }

    @Nested
    @DisplayName("should find by id")
    inner class ShouldFindById {

        @Test
        fun `with no item`() {
            /* Given */
            val id = fromString("9706ba78-2df2-4b37-a573-04367dc6f0ea")
            /* When */
            StepVerifier.create(repository.findById(id))
                    /* Then */
                    .expectSubscription()
                    .assertNext {
                        assertThat(it.name).isEqualTo("empty playlist")
                        assertThat(it.id).isEqualTo(id)
                        assertThat(it.items).isEmpty()
                    }
                    .verifyComplete()
        }

        @Test
        fun `with 1 item`() {
            /* Given */
            val id = fromString("24248480-bd04-11e5-a837-0800200c9a66")
            /* When */
            StepVerifier.create(repository.findById(id))
                    /* Then */
                    .expectSubscription()
                    .assertNext {
                        assertThat(it.name).isEqualTo("Conférence Rewind")
                        assertThat(it.id).isEqualTo(id)
                        assertThat(it.items).hasSize(1).containsOnly(
                                PlaylistWithItems. Item(
                                        id = fromString("0a774611-c857-44df-b7e0-5e5af31f7b56"),
                                        title = "Geek INC 124",
                                        fileName = "geekinc.124.mp3",
                                        description = "desc",
                                        mimeType = null,
                                        podcast = PlaylistWithItems.Item.Podcast(
                                                id = fromString("67b56578-454b-40a5-8d55-5fe1a14673e8"),
                                                title = "Geek Inc HD"
                                        ),
                                        cover = PlaylistWithItems.Item.Cover(
                                                id = fromString("9f050dc4-6a2e-46c3-8276-43098c011e68"),
                                                width = 100,
                                                height = 100,
                                                url = URI("http://fake.url.com/geekinc/cover.png")))
                        )
                    }
                    .verifyComplete()
        }

        @Test
        fun `with 2 items`() {
            /* Given */
            val id = fromString("dc024a30-bd02-11e5-a837-0800200c9a66")
            /* When */
            StepVerifier.create(repository.findById(id))
                    /* Then */
                    .expectSubscription()
                    .assertNext {
                        assertThat(it.name).isEqualTo("Humour Playlist")
                        assertThat(it.id).isEqualTo(id)
                        assertThat(it.items).hasSize(2).containsOnly(
                                PlaylistWithItems.Item(
                                        id = fromString("43fb990f-0b5e-413f-920c-6de217f9ecdd"),
                                        title = "Appload 3",
                                        fileName = "appload.3.mp3",
                                        description = "desc",
                                        mimeType = null,
                                        podcast = PlaylistWithItems.Item.Podcast(
                                                id = fromString("e9c89e7f-7a8a-43ad-8425-ba2dbad2c561"),
                                                title = "AppLoad"
                                        ),
                                        cover = PlaylistWithItems.Item.Cover(
                                                id = fromString("8ea0373e-7af6-4e15-b0fd-9ec4b10822ec"),
                                                width = 100,
                                                height = 100,
                                                url = URI("http://fake.url.com/appload/cover.png")
                                        )
                                ),
                                PlaylistWithItems. Item(
                                        id = fromString("0a774611-c857-44df-b7e0-5e5af31f7b56"),
                                        title = "Geek INC 124",
                                        fileName = "geekinc.124.mp3",
                                        description = "desc",
                                        mimeType = null,
                                        podcast = PlaylistWithItems.Item.Podcast(
                                                id = fromString("67b56578-454b-40a5-8d55-5fe1a14673e8"),
                                                title = "Geek Inc HD"
                                        ),
                                        cover = PlaylistWithItems.Item.Cover(
                                                id = fromString("9f050dc4-6a2e-46c3-8276-43098c011e68"),
                                                width = 100,
                                                height = 100,
                                                url = URI("http://fake.url.com/geekinc/cover.png")))
                        )
                    }
                    .verifyComplete()
        }
    }

    @TestConfiguration
    class LocalTestConfiguration {
        @Bean fun datasourceDestination(dataSource: DataSource) =  DataSourceDestination(dataSource)
    }
}
