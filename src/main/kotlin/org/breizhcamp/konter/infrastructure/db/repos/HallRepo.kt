package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface HallRepo: JpaRepository<HallDB, Int> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        INSERT INTO hall(name) VALUES (?) RETURNING id
    """, nativeQuery = true)
    fun create(name: String): Int

    @Query("""
        SELECT * FROM hall
        WHERE id IN (
            SELECT hall_id FROM available WHERE event_id = ?
        )
    """, nativeQuery = true)
    fun getAllByAvailableEventId(eventId: Int): List<HallDB>

}