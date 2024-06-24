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
        ORDER BY (SELECT "order" FROM available WHERE event_id = ?1 AND hall_id = id)
    """, nativeQuery = true)
    fun getAllByAvailableEventId(eventId: Int): List<HallDB>

    @Query("""
        SELECT h.* FROM hall h JOIN available a ON h.id = a.hall_id WHERE a.event_id = ?1 AND "order" > (
            SELECT "order" FROM available av WHERE av.hall_id = ?2 AND av.event_id = ?1
        )
    """, nativeQuery = true)
    fun getAllByOrderAfterHallInEvent(eventId: Int, hallId: Int): List<HallDB>

    @Modifying
    @Query("""
        UPDATE available SET "order" = "order" - 1 WHERE hall_id = ?1 AND event_id = ?2
    """, nativeQuery = true)
    fun decreaseOrder(id: Int, eventId: Int)

    @Modifying
    @Query("""
        INSERT INTO available(hall_id, event_id, "order") VALUES (?, ?, ?)
    """, nativeQuery = true)
    fun associateToEvent(id: Int, eventId: Int, order: Int)

    @Modifying
    @Query("""
        DELETE FROM available WHERE hall_id = ? AND event_id = ?
    """, nativeQuery = true)
    fun dissociateFromEvent(id: Int, eventId: Int)

    @Modifying
    @Query("""
        UPDATE available SET "order" = ?3 WHERE hall_id = ?1 AND event_id = ?2
    """, nativeQuery = true)
    fun setOrderInEvent(id: Int, eventId: Int, order: Int)

}