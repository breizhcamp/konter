package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.springframework.stereotype.Service

@Service
class HallCreate (
    private val hallPort: HallPort
) {
    fun createHall(req: HallCreationReq): Hall = hallPort.create(req)
}