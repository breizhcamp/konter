package org.breizhcamp.konter.infrastructure.db.model

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Duration
import java.time.LocalTime
import java.util.*

@Entity
@Table(name = "slot")
data class SlotDB(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    val day: Int,
    @OneToOne
    @JoinColumn(name = "session_id")
    val session: SessionDB?,
    @ManyToOne
    @JoinTable(
        name = "held",
        joinColumns = [JoinColumn(name = "slot_id")],
        inverseJoinColumns = [JoinColumn(name = "event_id")]
    )
    val event: EventDB,
    @ManyToMany
    @JoinTable(
        name = "held",
        joinColumns = [JoinColumn(name = "slot_id")],
        inverseJoinColumns = [JoinColumn(name = "hall_id")]
    )
    val halls: Set<HallDB>,
    val start: LocalTime,
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    val duration: Duration,
    val barcode: String?,
)
