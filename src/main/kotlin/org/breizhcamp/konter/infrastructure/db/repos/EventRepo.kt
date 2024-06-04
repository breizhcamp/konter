package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.EventDB
import org.breizhcamp.konter.infrastructure.db.model.SlotDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EventRepo: JpaRepository<EventDB, Int> {

    fun existsByYear(year: Int): Boolean
    fun findByYear(year: Int): EventDB
    @Query("""
        INSERT INTO event(year) VALUES(?) RETURNING id
    """, nativeQuery = true)
    fun createEvent(year: Int): Int

    @Query("""
        SELECT slot FROM SlotDB slot 
        WHERE slot.event.id = ?1
        ORDER BY slot.start ASC 
    """)
    fun getAllSlotsByEventId(eventId: Int): List<SlotDB>

}