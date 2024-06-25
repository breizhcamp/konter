package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Speaker
import org.breizhcamp.konter.infrastructure.db.model.SpeakerDB
import org.breizhcamp.konter.testUtils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SessionMapperTest {

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `toSession should map all fields one to one, except owner, speakers, event that are fully mapped and slot that is mapped to a limited version`(
        hasNotNullSlot: Boolean
    ) {
        var sessionDB = SessionDBGen().generateOne()
        if (hasNotNullSlot) {
            sessionDB = sessionDB.copy(slot = ImportSlotDBGen().generateOne())
        }
        val session = sessionDB.toSession()

        assertEquals(sessionDB.id,                      session.id)
        assertEquals(sessionDB.title,                   session.title)
        assertEquals(sessionDB.description,             session.description)

        // The SpeakerDB.toSpeaker() -> Speaker mapping is tested in the SpeakerMapperTest.kt file
        assertEquals(sessionDB.owner.toSpeaker(),       session.owner)
        assertEquals(sessionDB.speakers.map(SpeakerDB::toSpeaker),
                                                        session.speakers)

        assertEquals(sessionDB.format,                  session.format)
        assertEquals(sessionDB.theme,                   session.theme)
        assertEquals(sessionDB.niveau,                  session.niveau)
        assertEquals(sessionDB.status,                  session.status)
        assertEquals(sessionDB.submitted,               session.submitted)
        assertEquals(sessionDB.ownerNotes,              session.ownerNotes)
        assertEquals(sessionDB.videoURL,                session.videoURL)

        // The EventDB.toEvent() -> Event mapping is tested in the EventMapperTest.kt file
        assertEquals(sessionDB.event.toEvent(),         session.event)

        // The SlotDB.toLimitedSlot() -> Slot (limited) mapping is tested in the SlotMapperTest.kt file
        assertEquals(sessionDB.slot?.toLimitedSlot(),   session.slot)
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `toLimitedSession should map all fields one to one, except owner, speakers, event that are fully mapped and slot that is set to null`(
        hasNotNullSlot: Boolean
    ) {
        var sessionDB = SessionDBGen().generateOne()
        if (hasNotNullSlot) {
            sessionDB = sessionDB.copy(slot = ImportSlotDBGen().generateOne())
        }
        val session = sessionDB.toLimitedSession()

        assertEquals(sessionDB.id,                  session.id)
        assertEquals(sessionDB.title,               session.title)
        assertEquals(sessionDB.description,         session.description)

        // The SpeakerDB.toSpeaker() -> Speaker mapping is tested in the SpeakerMapperTest.kt file
        assertEquals(sessionDB.owner.toSpeaker(),   session.owner)
        assertEquals(sessionDB.speakers.map(SpeakerDB::toSpeaker),
                                                    session.speakers)

        assertEquals(sessionDB.format,              session.format)
        assertEquals(sessionDB.theme,               session.theme)
        assertEquals(sessionDB.niveau,              session.niveau)
        assertEquals(sessionDB.status,              session.status)
        assertEquals(sessionDB.submitted,           session.submitted)
        assertEquals(sessionDB.ownerNotes,          session.ownerNotes)
        assertEquals(sessionDB.videoURL,            session.videoURL)

        // The EventDB.toEvent() -> Event mapping is tested in the EventMapperTest.kt file
        assertEquals(sessionDB.event.toEvent(),     session.event)

        // In the limited mapping, the slot is always set to null in order to avoid circular dependant calls
        assertEquals(null,                  session.slot)
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `Session toDB should map all fields one to one, except owner, speakers, event and slot`(hasNotNullSlot: Boolean) {
        var session = SessionGen().generateOne()
        if (hasNotNullSlot) {
            val emptySlot = SlotGen().generateOne().copy(session = null)
            session = session.copy(slot = emptySlot)
        }
        val sessionDB = session.toDB()

        assertEquals(session.id,            sessionDB.id)
        assertEquals(session.title,         sessionDB.title)
        assertEquals(session.description,   sessionDB.description)

        // The Speaker.toDB -> SpeakerDB mapping is tested in the SpeakerMapperTest.kt file
        assertEquals(session.owner.toDB(),  sessionDB.owner)
        assertEquals(session.speakers.map(Speaker::toDB).toSet(),
                                            sessionDB.speakers)

        assertEquals(session.format,        sessionDB.format)
        assertEquals(session.theme,         sessionDB.theme)
        assertEquals(session.niveau,        sessionDB.niveau)
        assertEquals(session.status,        sessionDB.status)
        assertEquals(session.submitted,     sessionDB.submitted)
        assertEquals(session.ownerNotes,    sessionDB.ownerNotes)
        assertEquals(session.videoURL,      sessionDB.videoURL)

        // The Event.toDB() -> EventDB mapping is tested in the EventMapperTest.kt file
        assertEquals(session.event.toDB(),  sessionDB.event)

        // The Slot.toDB() -> SlotDB mapping is tested in the SlotMapperTest.kt file
        // We don't use a limited mapping, as object sent to the API are finite
        // and should then not have infinite recursive objects
        assertEquals(session.slot?.toDB(),  sessionDB.slot)
    }

    @Test
    fun `toManualSession should map all fields one to one except event that is fully mapped`() {
        val manualSessionDB = ManualSessionDBGen().generateOne()
        val manualSession = manualSessionDB.toManualSession()

        assertEquals(manualSessionDB.id, manualSession.id)
        assertEquals(manualSessionDB.title, manualSession.title)
        assertEquals(manualSessionDB.description, manualSession.description)

        // The EventDB.toEvent() -> Event mapping is tested in the EventMapperTest.kt file
        assertEquals(manualSessionDB.event.toEvent(), manualSession.event)

        assertEquals(manualSessionDB.format, manualSession.format)
        assertEquals(manualSessionDB.theme, manualSession.theme)
    }

    @Test
    fun `ManualSession toDB should map all fields one to one except event that is fully mapped`() {
        val manualSession = ManualSessionGen().generateOne()
        val manualSessionDB = manualSession.toDB()

        assertEquals(manualSession.id, manualSessionDB.id)
        assertEquals(manualSession.title, manualSessionDB.title)
        assertEquals(manualSession.description, manualSessionDB.description)

        // The Event.toDB() -> EventDB mapping is tested in the EventMapperTest.kt file
        assertEquals(manualSession.event.toDB(), manualSessionDB.event)

        assertEquals(manualSession.format, manualSessionDB.format)
        assertEquals(manualSession.theme, manualSessionDB.theme)
    }
}