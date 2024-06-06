package org.breizhcamp.konter.infrastructure.db.model

import jakarta.persistence.*
import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum

@Entity
@Table(name = "manual_session")
data class ManualSessionDB(
    @Id
    val id: Int,
    val title: String,
    val description: String,
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    val event: EventDB,
    val format: SessionFormatEnum,
    val theme: SessionThemeEnum,
)
