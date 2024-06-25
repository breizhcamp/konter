package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.testUtils.SpeakerDBGen
import org.breizhcamp.konter.testUtils.SpeakerGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SpeakerMapperTest {

    @Test
    fun `toSpeaker should convert all fields one to one`() {
        val speakerDB = SpeakerDBGen().generateOne()
        val speaker = speakerDB.toSpeaker()

        assertEquals(speakerDB.id,              speaker.id)
        assertEquals(speakerDB.lastname,        speaker.lastname)
        assertEquals(speakerDB.firstname,       speaker.firstname)
        assertEquals(speakerDB.email,           speaker.email)
        assertEquals(speakerDB.tagLine,         speaker.tagLine)
        assertEquals(speakerDB.bio,             speaker.bio)
        assertEquals(speakerDB.profilePicture,  speaker.profilePicture)
    }

    @Test
    fun toDB() {
        val speaker = SpeakerGen().generateOne()
        val speakerDB = speaker.toDB()

        assertEquals(speaker.id,                speakerDB.id)
        assertEquals(speaker.lastname,          speakerDB.lastname)
        assertEquals(speaker.firstname,         speakerDB.firstname)
        assertEquals(speaker.email,             speakerDB.email)
        assertEquals(speaker.tagLine,           speakerDB.tagLine)
        assertEquals(speaker.bio,               speakerDB.bio)
        assertEquals(speaker.profilePicture,    speakerDB.profilePicture)
    }
}