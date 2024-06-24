package org.breizhcamp.konter.domain.entities

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import java.time.LocalDateTime

data class Talk(
    val id: Int,
    val name: String,
    val eventStart: LocalDateTime,
    val eventEnd: LocalDateTime,
    val eventType: SessionThemeEnum,
    val format: SessionFormatEnum,
    val hall: Hall,
    val speakers: List<Speaker>,
    val videoUrl: String?,
    val filesUrl: String?,
    val slidesUrl: String?,
    val description: String?
)
