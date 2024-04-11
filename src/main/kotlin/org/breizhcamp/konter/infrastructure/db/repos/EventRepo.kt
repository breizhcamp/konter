package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.EventDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EventRepo: JpaRepository<EventDB, Int> {

    fun existsByYear(year: Int): Boolean
    fun findByYear(year: Int): EventDB
    @Query("""
        INSERT INTO event(year) VALUES(?) RETURNING id
    """, nativeQuery = true)
    fun createEvent(year: Int): Int
}