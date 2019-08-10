package com.github.davinkevin.podcastserver.service

import arrow.core.Try
import arrow.core.getOrElse
import com.github.davinkevin.podcastserver.entity.Cover
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream
import java.net.URI
import javax.imageio.ImageIO

/**
 * Created by kevin on 22/07/2018
 */
@Service
class ImageService(val urlService: UrlService) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)!!

    fun getCoverFromURL(imageUrl: String?): Cover? {
        if (imageUrl == null || imageUrl.isEmpty())  {
            return null
        }


        return Try { urlService.asStream(imageUrl).use { it.toBufferedImage() } }
                .map { Cover().apply {
                    url = imageUrl
                    height = it.height
                    width = it.width
                } }
                .getOrElse {
                    log.error("Error during fetching Cover information for {}", imageUrl)
                    null
                }
    }

    fun fetchCoverInformation(imageUrl: String?): CoverInformation? {
        val c = getCoverFromURL(imageUrl) ?: return null
        return CoverInformation(c.width!!, c.height!!, URI(c.url!!))
    }
}

data class CoverInformation(val width: Int, val height: Int, val url: URI)

private fun InputStream.toBufferedImage() = ImageIO.read(ImageIO.createImageInputStream(this))!!
