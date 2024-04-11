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
