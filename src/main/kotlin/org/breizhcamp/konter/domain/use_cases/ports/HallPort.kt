package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.application.requests.HallPatchReq
import org.breizhcamp.konter.domain.entities.Hall

interface HallPort {

    fun list(eventId: Int?): List<Hall>
    fun create(req: HallCreationReq): Hall
    fun associateToEvent(id: Int, eventId: Int): Hall
    fun dissociateFromEvent(id: Int, eventId: Int): Hall
    fun setOrderInEvent(id: Int, eventId: Int, order: Int?)
    fun update(id: Int, req: HallPatchReq): Hall
    fun delete(id: Int)

}