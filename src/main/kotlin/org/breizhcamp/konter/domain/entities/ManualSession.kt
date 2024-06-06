package org.breizhcamp.konter.domain.entities

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum

data class ManualSession(
    val id: Int,
    val title: String,
    val description: String,
    val event: Event,
    val format: SessionFormatEnum,
    val theme: SessionThemeEnum
)
