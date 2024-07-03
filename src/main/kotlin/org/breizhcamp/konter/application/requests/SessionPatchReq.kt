package org.breizhcamp.konter.application.requests

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum

data class SessionPatchReq(
    val title: String?,
    val description: String?,
    val format: SessionFormatEnum?,
    val theme: SessionThemeEnum?,
) {
    companion object {
        fun empty(): SessionPatchReq = SessionPatchReq(
            title = null,
            description = null,
            format = null,
            theme = null
        )
    }
}
