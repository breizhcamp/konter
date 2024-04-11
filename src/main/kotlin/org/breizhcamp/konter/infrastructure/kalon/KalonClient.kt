package org.breizhcamp.konter.infrastructure.kalon

import org.breizhcamp.konter.domain.entities.Event
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

interface KalonClient {

    @GetExchange("/api/events")
    fun getEventIds(): List<Int>

    @GetExchange("/api/events/{id}")
    fun getEventById(@PathVariable id: Int): Event

}