package org.breizhcamp.konter.infrastructure.db.model

import jakarta.persistence.*

@Entity
@Table(name = "event")
data class EventDB(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val year: Int,
    val name: String?,
)
