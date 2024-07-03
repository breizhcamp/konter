package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.application.requests.SessionCreationReq
import org.breizhcamp.konter.application.requests.SessionPatchReq
import org.breizhcamp.konter.domain.entities.ManualSession

interface ManualSessionPort {

    fun create(request: SessionCreationReq, eventId: Int): ManualSession
    fun getById(id: Int): ManualSession
    fun getAllByEventId(eventId: Int): List<ManualSession>
    fun update(id: Int, request: SessionPatchReq): ManualSession
    fun delete(id: Int)

}