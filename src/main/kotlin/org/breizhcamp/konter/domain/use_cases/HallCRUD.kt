package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.application.requests.HallPatchReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.springframework.stereotype.Service

@Service
class HallCRUD (
    private val hallPort: HallPort
) {
    fun create(req: HallCreationReq): Hall = hallPort.create(req)
    fun update(id: Int, req: HallPatchReq): Hall = hallPort.update(id, req)
    fun delete(id: Int) = hallPort.delete(id)
    fun listAll(): List<Hall> = hallPort.list(null)
    fun listByEvent(eventId: Int): List<Hall> = hallPort.list(eventId)
}