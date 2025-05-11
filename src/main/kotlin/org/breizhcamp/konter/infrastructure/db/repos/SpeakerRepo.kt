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

    @Query("""
        SELECT DISTINCT s 
        FROM SlotDB slot
        JOIN slot.session session
        JOIN session.speakers s
        WHERE slot.session IS NOT NULL
        ORDER BY s.lastname, s.firstname
    """)
    fun findAllWithSessionAndSlot(): List<SpeakerDB>

}
