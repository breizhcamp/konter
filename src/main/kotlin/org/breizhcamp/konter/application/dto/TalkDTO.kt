package org.breizhcamp.konter.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TalkDTO(
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val id: Int,
    val name: String,
    val eventStart: LocalDateTime,
    val eventEnd: LocalDateTime,
    val eventType: String,
    val format: String,
    val venue: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val venueId: Int,
    val speakers: String,
    val videoUrl: String?,
    val filesUrl: String?,
    val slidesUrl: String?,
    val description: String?
)
