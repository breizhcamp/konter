package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.SpeakerDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpeakerRepo: JpaRepository<SpeakerDB, UUID> {

    @Query("""
        SELECT speaker FROM SpeakerDB speaker
        WHERE CONCAT(speaker.firstname, ' ', speaker.lastname) = :name
        AND speaker.email = :email
    """)
    fun findByNameAndEmail(name: String, email: String): SpeakerDB

}