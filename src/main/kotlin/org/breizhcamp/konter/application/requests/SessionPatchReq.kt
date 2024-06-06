package org.breizhcamp.konter.application.requests

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum

data class SessionPatchReq(
    val title: String?,
    val description: String?,
    val format: SessionFormatEnum?,
    val theme: SessionThemeEnum?,
)
