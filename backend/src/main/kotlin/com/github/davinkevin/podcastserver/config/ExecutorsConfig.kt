package com.github.davinkevin.podcastserver.config

import com.github.davinkevin.podcastserver.service.properties.PodcastServerParameters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

/**
 * Created by kevin on 08/02/2014.
 */
@Configuration
@EnableAsync
class ExecutorsConfig(val parameters: PodcastServerParameters) {
    @Bean(name = ["DownloadExecutor"])
    fun downloadExecutor() = ThreadPoolTaskExecutor().apply {
        corePoolSize = parameters.concurrentDownload
        setThreadNamePrefix("Downloader-")
        initialize()
    }
}
