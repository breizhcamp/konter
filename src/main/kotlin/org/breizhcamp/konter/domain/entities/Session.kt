package org.breizhcamp.konter.domain.entities

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionNiveauEnum
import org.breizhcamp.konter.domain.entities.enums.SessionStatusEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import java.math.BigDecimal
import java.time.LocalDateTime

data class Session(
    val id: Int,
    val title: String,
    val description: String,
    val owner: Speaker,
    val speakers: List<Speaker>,
    val format: SessionFormatEnum,
    val theme: SessionThemeEnum,
    val niveau: SessionNiveauEnum,
    val status: SessionStatusEnum,
    val submitted: LocalDateTime,
    val ownerNotes: String,
    val event: Event,
    val beginning: LocalDateTime?,
    val end: LocalDateTime?,
    val videoURL: String?,
    val rating: BigDecimal?,
    val slot: Slot?
)
