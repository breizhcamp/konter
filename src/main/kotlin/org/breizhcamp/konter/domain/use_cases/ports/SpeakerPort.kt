package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.domain.entities.Speaker
import java.util.*

interface SpeakerPort {

    fun list(): List<Speaker>
    fun get(id: UUID): Speaker
    fun getByNameAndEmail(name: String, email: String): Speaker
    fun save(speaker: Speaker)

}