package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.domain.entities.Event

interface KalonPort {

    fun getEvents(): List<Event>

}