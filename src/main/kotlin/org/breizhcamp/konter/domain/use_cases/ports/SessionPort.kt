package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.domain.entities.Evaluation
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter

interface SessionPort {

    fun getById(id: Int): Session
    fun save(session: Session)
    fun getAllByEventYear(year: Int): List<Session>
    fun saveEvaluation(evaluation: Evaluation)
    fun filterByEventYear(year: Int, filter: SessionFilter): List<Session>

}