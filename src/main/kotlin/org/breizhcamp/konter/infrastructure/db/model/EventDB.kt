package org.breizhcamp.konter.infrastructure.db.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "event")
data class EventDB(
    @Id
    val id: Int,
    val year: Int,
    val name: String?,
    @ManyToMany
    @JoinTable(
        name = "available",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "hall_id")]
    )
    val halls: Set<HallDB>,
    @Column(name = "event_begin")
    val begin: LocalDate?,
    @Column(name = "event_end")
    val end: LocalDate?
)
