package org.breizhcamp.konter.application.dto

import java.time.Duration
import java.time.LocalTime
import java.util.*

data class SlotDTO(
    val id: UUID,
    val day: Int,
    val session: SessionDTO?,
    val halls: List<HallDTO>,
    val start: LocalTime,
    val duration: Duration,
    val barcode: String?,
    val span: Int
)
