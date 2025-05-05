package org.breizhcamp.konter.domain.entities

import java.time.LocalTime

data class Assignation(
    val sessionId: Int,
    val day: Int,
    val start: LocalTime,
    val room: String,
)
