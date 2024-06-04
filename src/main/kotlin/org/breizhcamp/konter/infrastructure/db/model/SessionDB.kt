package org.breizhcamp.konter.infrastructure.db.model

import jakarta.persistence.*
import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionNiveauEnum
import org.breizhcamp.konter.domain.entities.enums.SessionStatusEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "session")
data class SessionDB(
    @Id
    val id: Int,
    val title: String,
    val description: String,
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: SpeakerDB,
    @ManyToMany
    @JoinTable(
        name = "presents",
        joinColumns = [JoinColumn(name = "session_id")],
        inverseJoinColumns = [JoinColumn(name = "speaker_id")]
    )
    val speakers: Set<SpeakerDB>,
    @Enumerated(EnumType.ORDINAL)
    val format: SessionFormatEnum,
    @Enumerated(EnumType.ORDINAL)
    val theme: SessionThemeEnum,
    @Enumerated(EnumType.ORDINAL)
    val niveau: SessionNiveauEnum,
    @Enumerated(EnumType.ORDINAL)
    val status: SessionStatusEnum,
    val submitted: LocalDateTime,
    @Column(name = "owner_notes")
    val ownerNotes: String,
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    val event: EventDB,
    val beginning: LocalDateTime?,
    @Column(name = "\"end\"")
    val end: LocalDateTime?,
    @Column(name = "video_url")
    val videoURL: String?,
    val rating: BigDecimal?,
    val barcode: String?,
    @OneToOne(
        targetEntity = SlotDB::class,
        mappedBy = "session"
    )
    val slot: SlotDB?
)
