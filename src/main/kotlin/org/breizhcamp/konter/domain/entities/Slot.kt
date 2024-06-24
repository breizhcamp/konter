package org.breizhcamp.konter.domain.entities

import java.time.Duration
import java.time.LocalTime
import java.util.*

data class Slot(
    val id: UUID,
    val day: Int,
    val session: Session?,
    val manualSession: ManualSession?,
    val event: Event,
    val halls: List<Hall>,
    val start: LocalTime,
    val duration: Duration,
    val barcode: String?,
    val span: Int,
    val title: String?,
    val assignable: Boolean
)
