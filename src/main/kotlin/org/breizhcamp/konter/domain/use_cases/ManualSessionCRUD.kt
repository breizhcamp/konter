package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.application.requests.SessionCreationReq
import org.breizhcamp.konter.application.requests.SessionPatchReq
import org.breizhcamp.konter.domain.use_cases.ports.ManualSessionPort
import org.springframework.stereotype.Service

@Service
class ManualSessionCRUD(
    private val manualSessionPort: ManualSessionPort
) {

    fun create(request: SessionCreationReq, eventId: Int) =
        manualSessionPort.create(request, eventId)

    fun get(id: Int) =
        manualSessionPort.getById(id)

    fun list(eventId: Int) =
        manualSessionPort.getAllByEventId(eventId)

    fun update(id: Int, request: SessionPatchReq) =
        manualSessionPort.update(id, request)

    fun delete(id: Int) =
         manualSessionPort.delete(id)
}