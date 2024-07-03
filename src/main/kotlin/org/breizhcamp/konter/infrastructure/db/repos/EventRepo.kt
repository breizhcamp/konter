package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.EventDB
import org.breizhcamp.konter.infrastructure.db.model.SlotDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EventRepo: JpaRepository<EventDB, Int> {

    @Query("""
        SELECT slot FROM SlotDB slot WHERE slot.event.id = ?1
    """)
    fun listSlotsHeld(id: Int): List<SlotDB>
}