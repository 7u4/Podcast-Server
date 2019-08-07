package com.github.davinkevin.podcastserver.manager.worker.upload

import com.github.davinkevin.podcastserver.entity.Item
import com.github.davinkevin.podcastserver.entity.Podcast
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI
import java.util.*

/**
 * Created by kevin on 28/06/15 for Podcast Server
 */
class UploadUpdaterTest {

    private val item1 = Item().apply { id = UUID.randomUUID() }
    private val item2 = Item().apply { id = UUID.randomUUID() }
    private val item3 = Item().apply { id = UUID.randomUUID() }
    private var podcast: Podcast = Podcast().apply {
        url = "http://foo.bar.com"
        add(item1)
        add(item2)
        add(item3)
    }

    val updater = UploadUpdater()
    
    @Test
    fun `should serve items`() {
        assertThat(updater.findItems(podcast))
                .hasSize(3)
                .contains(item1, item2, item3)
    }

    @Test
    fun `should generate an empty signature`() {
        assertThat(updater.signatureOf(URI(podcast.url!!)))
                .isEmpty()
    }

    @Test
    fun `should reject every item`() {
        assertThat(updater.notIn(podcast)(Item()))
                .isFalse()
    }

    @Test
    fun `should show his type`() {
        
        /* When */
        val type = updater.type()
        
        /* Then */
        assertThat(type).isSameAs(UploadUpdater.TYPE)
        assertThat(type.key).isEqualTo("upload")
        assertThat(type.name).isEqualTo("Upload")
    }

    @Test
    fun `should not be compatible`() {
        assertThat(updater.compatibility("http://foo/bar")).isEqualTo(Integer.MAX_VALUE)
    }
}
