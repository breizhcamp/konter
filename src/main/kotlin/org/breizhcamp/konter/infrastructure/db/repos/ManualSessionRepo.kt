package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import org.breizhcamp.konter.infrastructure.db.model.ManualSessionDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ManualSessionRepo: JpaRepository<ManualSessionDB, Int> {

    @Modifying
    @Query("""
        INSERT INTO manual_session(event_id, title, description, format, theme)
        VALUES (?, ?, ?, ?, ?)
        RETURNING id
    """, nativeQuery = true)
    fun create(
        eventId: Int,
        title: String,
        description: String,
        format: SessionFormatEnum,
        theme: SessionThemeEnum
    ): Int

    @Query("""
        SELECT mSession FROM ManualSessionDB mSession WHERE mSession.event.id = ?1
    """)
    fun getAllByEventId(eventId: Int): List<ManualSessionDB>
}