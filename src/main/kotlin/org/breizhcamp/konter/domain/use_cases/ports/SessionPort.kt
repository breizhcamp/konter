package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.domain.entities.Evaluation
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import java.util.*

interface SessionPort {

    fun getById(id: Int): Session
    fun import(session: Session)
    fun getAllByEventId(eventId: Int, sortByFormat: Boolean): List<Session>
    fun saveEvaluation(evaluation: Evaluation)
    fun filterByEventId(eventId: Int, filter: SessionFilter): List<Session>
    fun addBarcode(id: Int, barcode: String)
    fun setSlotById(id: Int, slotId: UUID): Session
    fun setSlotByBarcode(id: Int, barcode: String): Session

}