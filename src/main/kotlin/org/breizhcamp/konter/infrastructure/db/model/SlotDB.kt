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
    @OneToOne
    @JoinColumn(name = "manual_session_id")
    val manualSession: ManualSessionDB?,
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
    val title: String?,
    val assignable: Boolean
) {
    override fun hashCode(): Int {
        var hash = 0

        for (item in this.javaClass.fields.filter {
            it.name != SlotDB::session::name.toString() &&
                    it.name != SlotDB::manualSession::name.toString()
        }) {
            hash += item.hashCode()
            hash *= 32
        }

        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SlotDB

        if (id != other.id) return false
        if (day != other.day) return false
        if (event != other.event) return false
        if (halls != other.halls) return false
        if (start != other.start) return false
        if (duration != other.duration) return false
        if (barcode != other.barcode) return false
        if (title != other.title) return false

        return true
    }
}
