package org.breizhcamp.konter.infrastructure.db

import org.breizhcamp.konter.domain.entities.Evaluation
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.breizhcamp.konter.infrastructure.db.mappers.toDB
import org.breizhcamp.konter.infrastructure.db.mappers.toSession
import org.breizhcamp.konter.infrastructure.db.repos.SessionRepo
import org.springframework.stereotype.Component

@Component
class SessionAdapter (
    private val sessionRepo: SessionRepo
): SessionPort {
    override fun getById(id: Int): Session =
        sessionRepo.findById(id).get().toSession()

    override fun save(session: Session) {
        sessionRepo.save(session.toDB())
    }

    override fun getAllByEventYear(year: Int): List<Session> =
        sessionRepo.findAllByEventYear(year).map { it.toSession() }

    override fun saveEvaluation(evaluation: Evaluation) {
        val session = evaluation.session.copy(rating = evaluation.rating)
        sessionRepo.save(session.toDB())
    }

    override fun filterByEventYear(year: Int, filter: SessionFilter): List<Session> =
        sessionRepo.filter(year, filter).map { it.toSession() }
}