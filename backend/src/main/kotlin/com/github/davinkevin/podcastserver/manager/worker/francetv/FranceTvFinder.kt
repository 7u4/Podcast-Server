package com.github.davinkevin.podcastserver.manager.worker.francetv

import com.github.davinkevin.podcastserver.entity.Cover
import com.github.davinkevin.podcastserver.entity.Podcast
import com.github.davinkevin.podcastserver.manager.worker.Finder
import com.github.davinkevin.podcastserver.service.HtmlService
import com.github.davinkevin.podcastserver.service.ImageService
import com.github.davinkevin.podcastserver.service.UrlService
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service
import java.util.*


/**
 * Created by kevin on 08/03/2016 for Podcast Server
 */
@Service
class FranceTvFinder(val htmlService: HtmlService, val imageService: ImageService) : Finder {

    override fun find(url: String) =
            htmlService.get(url)
                    .map { htmlToPodcast(it) }
                    .getOrElse(Podcast.DEFAULT_PODCAST)

    private fun htmlToPodcast(d: Document) =
            Podcast().apply {
                title = d.select("meta[property=og:title]").attr("content")
                description = d.select("meta[property=og:description]").attr("content")
                type = "FranceTv"
                cover = getCover(d)
                url = d.select("meta[property=og:url]")
                        .attr("content")
                        .addProtocolIfNecessary("https:")
            }

    private fun getCover(p: Document) =
            Optional.ofNullable(p.select("meta[property=og:image]"))
                    .map { it.attr("content") }
                    .map { it.addProtocolIfNecessary("https:") }
                    .flatMap { Optional.ofNullable(imageService.getCoverFromURL(it)) }
                    .orElse(Cover.DEFAULT_COVER)

    override fun compatibility(url: String?) = FranceTvUpdater.isFromFranceTv(url)
}

private fun String.addProtocolIfNecessary(protocol: String) =
        UrlService.addProtocolIfNecessary(protocol, this)
