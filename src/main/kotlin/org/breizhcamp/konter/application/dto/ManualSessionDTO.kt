package org.breizhcamp.konter.application.dto

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum

data class ManualSessionDTO(
    val id: Int,
    val title: String,
    val description: String,
    val format: SessionFormatEnum,
    val theme: SessionThemeEnum
)
