package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.domain.entities.Hall

interface HallPort {

    fun list(eventId: Int?): List<Hall>
    fun create(req: HallCreationReq): Hall

}