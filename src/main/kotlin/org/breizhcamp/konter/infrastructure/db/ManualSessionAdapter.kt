package org.breizhcamp.konter.infrastructure.db

import jakarta.transaction.Transactional
import org.breizhcamp.konter.application.requests.SessionCreationReq
import org.breizhcamp.konter.application.requests.SessionPatchReq
import org.breizhcamp.konter.domain.entities.ManualSession
import org.breizhcamp.konter.domain.use_cases.ports.ManualSessionPort
import org.breizhcamp.konter.infrastructure.db.mappers.toManualSession
import org.breizhcamp.konter.infrastructure.db.repos.ManualSessionRepo
import org.springframework.stereotype.Component


@Component
class ManualSessionAdapter(
    private val manualSessionRepo: ManualSessionRepo
) : ManualSessionPort {

    @Transactional
    override fun create(request: SessionCreationReq, eventId: Int): ManualSession {
        val id = manualSessionRepo.create(
            eventId,
            request.title,
            request.description,
            request.format,
            request.theme
        )

        return getById(id)
    }

    override fun getById(id: Int): ManualSession =
        manualSessionRepo.findById(id).get().toManualSession()

    override fun getAllByEventId(eventId: Int): List<ManualSession> =
        manualSessionRepo.getAllByEventId(eventId).map { it.toManualSession() }

    @Transactional
    override fun update(id: Int, request: SessionPatchReq): ManualSession {
        var session = manualSessionRepo.findById(id).get()

        request.title?.let { session = session.copy(title = it) }
        request.description?.let { session = session.copy(description = it) }
        request.format?.let { session = session.copy(format = it) }
        request.theme?.let { session = session.copy(theme = it) }

        return manualSessionRepo.save(session).toManualSession()
    }

    @Transactional
    override fun delete(id: Int) =
        manualSessionRepo.deleteById(id)
}