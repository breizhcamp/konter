package org.breizhcamp.konter.infrastructure.db.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "speaker")
data class SpeakerDB (
    @Id
    val id: UUID,
    val lastname: String,
    val firstname: String,
    val email: String,
    @Column(name = "tag_line")
    val tagLine: String,
    val bio: String,
    @Column(name = "profile_picture")
    val profilePicture: String,
)