package org.breizhcamp.konter.infrastructure.kalon

import mu.KotlinLogging
import org.breizhcamp.konter.config.KonterConfig
import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.domain.use_cases.ports.KalonPort
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

private val logger = KotlinLogging.logger {  }

@Component
class KalonAdapter (
    private val config: KonterConfig
): KalonPort {
    private val kalonClient = createClient()

    override fun getEvents(): List<Event> {
        logger.info { "Calling Kalon to get all existing events' ids" }
        val eventIds = kalonClient.getEventIds()

        logger.info { "Calling Kalon to get [${eventIds.size}] events" }
        return eventIds.map { kalonClient.getEventById(it) }
    }

    private fun createClient(): KalonClient {
        var webClientBuilder = WebClient.builder().baseUrl(config.kalon.url)

        if (config.kalon.secured) {
            val apiKey = requireNotNull(config.kalon.apiKey) { "Config error, Kalon apiKey is missing" }
            webClientBuilder = webClientBuilder.defaultHeader("Authorization", "Basic $apiKey")
        }

        val client = webClientBuilder.build()
        val factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build()
        return factory.createClient(KalonClient::class.java)
    }
}