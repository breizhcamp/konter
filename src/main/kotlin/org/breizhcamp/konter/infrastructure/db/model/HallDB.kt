package org.breizhcamp.konter.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "hall")
data class HallDB(
    @Id
    val id: Int,
    val name: String?,
)
