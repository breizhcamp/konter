package org.breizhcamp.konter.application.dto

import java.util.*

data class SpeakerDTO(
    val id: UUID,
    val lastname: String,
    val firstname: String,
    val email: String,
    val tagLine: String,
    val bio: String,
    val profilePicture: String,
)

data class SpeakerExportDTO(
    val id: UUID,
    val lastname: String,
    val firstname: String,
    val imageProfilURL: String?,
    val bio: String,
    val github: String?,
    val googleplus: String?,
    val twitter: String?,
    val social: String?
)
