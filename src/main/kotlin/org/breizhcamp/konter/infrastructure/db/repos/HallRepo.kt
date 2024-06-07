package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

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

}