package org.breizhcamp.konter.application.dto

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionNiveauEnum
import org.breizhcamp.konter.domain.entities.enums.SessionStatusEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import java.math.BigDecimal
import java.time.LocalDateTime

data class SessionDTO(
    val id: Int,
    val title: String,
    val description: String,
    val owner: SpeakerDTO,
    val speakers: List<SpeakerDTO>,
    val format: SessionFormatEnum,
    val theme: SessionThemeEnum,
    val niveau: SessionNiveauEnum,
    val status: SessionStatusEnum,
    val submitted: LocalDateTime,
    val ownerNotes: String,
    val beginning: LocalDateTime?,
    val end: LocalDateTime?,
    val videoURL: String?,
    val rating: BigDecimal?,
    val slot: SlotDTO?,
)
