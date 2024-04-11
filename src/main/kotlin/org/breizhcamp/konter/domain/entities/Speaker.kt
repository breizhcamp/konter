package org.breizhcamp.konter.domain.entities

import java.util.*

data class Speaker(
    val id: UUID,
    val lastname: String,
    val firstname: String,
    val email: String,
    val tagLine: String,
    val bio: String,
    val profilePicture: String,
)
