package org.breizhcamp.konter.infrastructure.db

import jakarta.transaction.Transactional
import org.breizhcamp.konter.domain.entities.Evaluation
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.breizhcamp.konter.infrastructure.db.mappers.toDB
import org.breizhcamp.konter.infrastructure.db.mappers.toSession
import org.breizhcamp.konter.infrastructure.db.model.SessionDB
import org.breizhcamp.konter.infrastructure.db.repos.SessionRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class SessionAdapter (
    private val sessionRepo: SessionRepo
): SessionPort {
    override fun getById(id: Int): Session =
        sessionRepo.findById(id).get().toSession()

    override fun import(session: Session) {
        var toSave: SessionDB = session.toDB()

        if (sessionRepo.existsById(session.id)) {
            val saved = sessionRepo.findById(session.id).get()
            toSave = toSave.copy(
                slot = saved.slot,
                beginning = saved.beginning,
                end = saved.end,
                videoURL = saved.videoURL,
                barcode = saved.barcode,
            )
        }

        sessionRepo.save(toSave)
    }

    override fun getAllByEventId(eventId: Int, sortByFormat: Boolean): List<Session> =
        sessionRepo.filter(eventId, SessionFilter.empty(), sortByFormat).map { it.toSession() }

    @Transactional
    override fun saveEvaluation(evaluation: Evaluation) {
        val session = evaluation.session.copy(rating = evaluation.rating)
        sessionRepo.save(session.toDB())
    }

    override fun filterByEventId(eventId: Int, filter: SessionFilter): List<Session> =
        sessionRepo.filter(eventId, filter, false).map { it.toSession() }

    @Transactional
    override fun addBarcode(id: Int, barcode: String) {
        sessionRepo.addBarcode(id, barcode)
    }

    @Transactional
    override fun setSlotById(id: Int, slotId: UUID): Session {
        sessionRepo.setSlot(id, slotId)
        return this.getById(id)
    }

    @Transactional
    override fun setSlotByBarcode(id: Int, barcode: String): Session {
        val slotId = sessionRepo.getSlotIdByBarcode(barcode)
        return setSlotById(id, slotId)
    }
}