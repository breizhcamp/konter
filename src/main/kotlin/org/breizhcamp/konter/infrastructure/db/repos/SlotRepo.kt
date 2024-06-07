package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.SlotDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalTime
import java.util.*

interface SlotRepo: JpaRepository<SlotDB, UUID> {

    @Modifying
    @Query("""
        WITH s_id as (
            INSERT INTO slot(day, start, duration, barcode, id)
            VALUES (?3, CAST(?4 AS TIME), ?5 * INTERVAL '1 second', ?6, gen_random_uuid())
            RETURNING id
        )
        INSERT INTO held(hall_id, event_id, slot_id) 
        SELECT ?1, ?2, s_id.id FROM s_id
    """, nativeQuery = true)
    fun create(hallId: Int, eventId: Int, day: Int, start: LocalTime, duration: Long, barcode: String?)

    fun getAllByEventId(eventId: Int): List<SlotDB>

    @Query("""
        SELECT slot FROM SlotDB slot WHERE slot.event.id = ?2 AND ?1 IN (
            SELECT hall.id FROM slot.halls hall
        )
    """)
    fun getByHallIdAndEventId(hallId: Int, eventId: Int): List<SlotDB>

    fun getByBarcodeAndEventId(barcode: String, eventId: Int): SlotDB

    @Modifying
    @Query("""
        INSERT INTO held(slot_id, hall_id, event_id) VALUES (?1, ?2, ?3)
    """, nativeQuery = true)
    fun associateToHallAndEvent(slotId: UUID, hallId: Int, eventId: Int)

    @Modifying
    @Query("""
        DELETE FROM held WHERE slot_id = ?1 and hall_id = ?2
    """, nativeQuery = true)
    fun dissocateFromHall(slotId: UUID, hallId: Int)
}