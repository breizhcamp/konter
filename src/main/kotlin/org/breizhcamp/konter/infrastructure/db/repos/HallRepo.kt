package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.breizhcamp.konter.infrastructure.db.model.SlotDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalTime
import java.util.*

@Repository
interface HallRepo: JpaRepository<HallDB, Int> {

    @Query("""
        INSERT INTO hall(name) VALUES (?) RETURNING id
    """, nativeQuery = true)
    fun create(name: String): Int

    @Modifying
    @Query("""
        UPDATE HallDB hall SET hall.trackId = ?2 WHERE hall.id = ?1
    """)
    fun addTrackIdToHall(id: Int, trackId: Int)

    @Query("""
        SELECT * FROM hall
        WHERE id IN (
            SELECT hall_id FROM available WHERE event_id = ?1
        )
        ORDER BY (
            -- Order by the ORDER column only if all halls available for the event have an order set, defaults to trackId
            CASE WHEN (
                SELECT count(*) FROM available WHERE event_id = ?1 AND "order" IS NOT NULL
            ) = (
                SELECT count(*) FROM available WHERE event_id = ?1
            ) 
                THEN (SELECT "order" FROM available WHERE event_id = ?1 AND hall_id = id) 
                ELSE track_id 
            END
        )
    """, nativeQuery = true)
    fun getAllByAvailableEventId(eventId: Int): List<HallDB>

    @Modifying
    @Query("""
        INSERT INTO available(hall_id, event_id) VALUES (?, ?)
    """, nativeQuery = true)
    fun associateToEvent(id: Int, eventId: Int)

    @Modifying
    @Query("""
        DELETE FROM available WHERE hall_id = ? AND event_id = ?
    """, nativeQuery = true)
    fun dissociateFromEvent(id: Int, eventId: Int)

    @Modifying
    @Query("""
        UPDATE available SET "order" = ?3 WHERE hall_id = ?1 AND event_id = ?2
    """, nativeQuery = true)
    fun setOrderInEvent(id: Int, eventId: Int, order: Int?)

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
    fun addSlotToHall(id: Int, eventId: Int, day: Int, start: LocalTime, duration: Long, barcode: String?)

    @Query("""
        SELECT slot FROM SlotDB slot WHERE slot.event.id = ?2 AND ?1 IN (
            SELECT hall.id FROM slot.halls hall
        )
    """)
    fun getSlotsByHallIdAndEventId(hallId: Int, eventId: Int): List<SlotDB>

    @Query("""
        SELECT slot FROM SlotDB slot WHERE slot.barcode = ?1
    """)
    fun getSlotByBarcode(barcode: String): SlotDB

    @Modifying
    @Query("""
        INSERT INTO held(hall_id, event_id, slot_id) VALUES (?1, ?2, ?3)
    """, nativeQuery = true)
    fun associateSlot(hallId: Int, eventId: Int, slotId: UUID)

    @Modifying
    @Query("""
        DELETE FROM held WHERE slot_id = ?2 and hall_id = ?1
    """, nativeQuery = true)
    fun dissociateSlot(hallId: Int, slotId: UUID)

    @Query("""
        SELECT slot FROM SlotDB slot WHERE slot.id = ?1
    """)
    fun getSlotById(id: UUID): SlotDB

    @Modifying
    @Query("""
        DELETE SlotDB slot WHERE slot.id = ?1
    """)
    fun removeSlot(slotId: UUID)

}