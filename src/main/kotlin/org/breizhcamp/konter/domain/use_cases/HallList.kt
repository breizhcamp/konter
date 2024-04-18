package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.springframework.stereotype.Service

@Service
class HallList (
    private val hallPort: HallPort
) {
    fun listAll(): List<Hall> = hallPort.list(null)
    fun listByEvent(eventId: Int): List<Hall> = hallPort.list(eventId)
}