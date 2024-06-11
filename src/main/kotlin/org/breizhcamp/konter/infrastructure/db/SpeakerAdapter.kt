package org.breizhcamp.konter.infrastructure.db

import org.breizhcamp.konter.domain.entities.Speaker
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.breizhcamp.konter.infrastructure.db.mappers.toDB
import org.breizhcamp.konter.infrastructure.db.mappers.toSpeaker
import org.breizhcamp.konter.infrastructure.db.repos.SpeakerRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class SpeakerAdapter (
    private val speakerRepo: SpeakerRepo
): SpeakerPort {
    override fun list(): List<Speaker> =
        speakerRepo.findAll().map { it.toSpeaker() }

    override fun get(id: UUID): Speaker =
        speakerRepo.findById(id).get().toSpeaker()

    override fun getByNameAndEmail(name: String, email: String): Speaker =
        speakerRepo.findByNameAndEmail(name, email).toSpeaker()

    override fun save(speaker: Speaker) {
        speakerRepo.save(speaker.toDB())
    }

}