package org.breizhcamp.konter.domain.entities

import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionNiveauEnum
import org.breizhcamp.konter.domain.entities.enums.SessionStatusEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum

data class SessionFilter(
    val id: Int?,
    val title: String?,
    val speakerName: String?,
    val format: SessionFormatEnum?,
    val theme: SessionThemeEnum?,
    val niveau: SessionNiveauEnum?,
    val status: SessionStatusEnum?,
    val rated: Boolean?
) {
    companion object {
        fun empty(): SessionFilter = SessionFilter(
            id = null,
            title = null,
            speakerName = null,
            format = null,
            theme = null,
            niveau = null,
            status = null,
            rated = null
        )
    }
}
