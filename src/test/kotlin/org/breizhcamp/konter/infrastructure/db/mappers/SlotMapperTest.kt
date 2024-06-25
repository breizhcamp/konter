package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.breizhcamp.konter.testUtils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class SlotMapperTest {

    enum class SlotContentCases {
        NoSessionNorManualSession,
        NoSessionButManualSession,
        SessionButNoManualSession,
        SessionAndManualSession
    }

    @ParameterizedTest
    @EnumSource(SlotContentCases::class)
    fun toSlot(case: SlotContentCases) {
        var slotDB = ImportSlotDBGen().generateOne()

        slotDB = when(case) {
             SlotContentCases.SessionAndManualSession,
             SlotContentCases.SessionButNoManualSession -> {
                val sessionDB = SessionDBGen().generateOne().copy(slot = null)
                slotDB.copy(session = sessionDB)
            }
            SlotContentCases.NoSessionNorManualSession,
            SlotContentCases.NoSessionButManualSession -> {
                slotDB.copy(session = null)
            }
        }

        slotDB = when(case) {
            SlotContentCases.SessionAndManualSession,
            SlotContentCases.NoSessionButManualSession -> {
                val manualSessionDB = ManualSessionDBGen().generateOne()
                slotDB.copy(manualSession = manualSessionDB)
            }
            SlotContentCases.SessionButNoManualSession,
            SlotContentCases.NoSessionNorManualSession -> {
                slotDB.copy(manualSession = null)
            }
        }

        val slot = slotDB.toSlot()

        assertEquals(slotDB.id, slot.id)
        assertEquals(slotDB.day, slot.day)
        assertEquals(slotDB.start, slot.start)
        assertEquals(slotDB.duration, slot.duration)

        // The HallDB.toHall() -> Hall mapping is tested in the HallMapperTest.kt file
        assertEquals(slotDB.halls.map(HallDB::toHall), slot.halls)

        // The SessionDB.toLimitedSession() -> Session limited mapping
        // and the ManualSessionDB.toManualSession() -> ManualSession mapping
        // are tested in the SessionMapper.kt file
        assertEquals(slotDB.session?.toLimitedSession(), slot.session)
        assertEquals(slotDB.manualSession?.toManualSession(), slot.manualSession)

        // The EventDB.toEvent() -> Event mapping is tested in the EventMapperTest.kt file
        assertEquals(slotDB.event.toEvent(), slot.event)

        assertEquals(slotDB.barcode, slot.barcode)
        assertEquals(1, slot.span)
        assertEquals(slotDB.title, slot.title)
    }

    @ParameterizedTest
    @EnumSource(SlotContentCases::class)
    fun toLimitedSlot(case: SlotContentCases) {
        var slotDB = ImportSlotDBGen().generateOne()

        slotDB = when(case) {
            SlotContentCases.SessionAndManualSession,
            SlotContentCases.SessionButNoManualSession -> {
                val sessionDB = SessionDBGen().generateOne().copy(slot = null)
                slotDB.copy(session = sessionDB)
            }
            SlotContentCases.NoSessionNorManualSession,
            SlotContentCases.NoSessionButManualSession -> {
                slotDB.copy(session = null)
            }
        }

        slotDB = when(case) {
            SlotContentCases.SessionAndManualSession,
            SlotContentCases.NoSessionButManualSession -> {
                val manualSessionDB = ManualSessionDBGen().generateOne()
                slotDB.copy(manualSession = manualSessionDB)
            }
            SlotContentCases.SessionButNoManualSession,
            SlotContentCases.NoSessionNorManualSession -> {
                slotDB.copy(manualSession = null)
            }
        }

        val slot = slotDB.toLimitedSlot()

        assertEquals(slotDB.id, slot.id)
        assertEquals(slotDB.day, slot.day)
        assertEquals(slotDB.start, slot.start)
        assertEquals(slotDB.duration, slot.duration)

        // The HallDB.toHall() -> Hall mapping is tested in the HallMapperTest.kt file
        assertEquals(slotDB.halls.map(HallDB::toHall), slot.halls)

        // In the limited mapping, the session and manualSession
        // are always set to null in order to avoid circular dependant calls
        assertEquals(null, slot.session)
        assertEquals(null, slot.manualSession)

        // The EventDB.toEvent() -> Event mapping is tested in the EventMapperTest.kt file
        assertEquals(slotDB.event.toEvent(), slot.event)

        assertEquals(slotDB.barcode, slot.barcode)
        assertEquals(1, slot.span)
        assertEquals(slotDB.title, slot.title)
    }

    @ParameterizedTest
    @EnumSource(SlotContentCases::class)
    fun toDB(case: SlotContentCases) {
        var slot = SlotGen().generateOne()

        slot = when(case) {
            SlotContentCases.SessionAndManualSession,
            SlotContentCases.SessionButNoManualSession -> {
                val session = SessionGen().generateOne().copy(slot = null)
                slot.copy(session = session)
            }
            SlotContentCases.NoSessionNorManualSession,
            SlotContentCases.NoSessionButManualSession -> {
                slot.copy(session = null)
            }
        }

        slot = when(case) {
            SlotContentCases.SessionAndManualSession,
            SlotContentCases.NoSessionButManualSession -> {
                val manualSession = ManualSessionGen().generateOne()
                slot.copy(manualSession = manualSession)
            }
            SlotContentCases.SessionButNoManualSession,
            SlotContentCases.NoSessionNorManualSession -> {
                slot.copy(manualSession = null)
            }
        }

        val slotDB = slot.toDB()

        assertEquals(slot.id, slotDB.id)
        assertEquals(slot.day, slotDB.day)
        assertEquals(slot.start, slotDB.start)
        assertEquals(slot.duration, slotDB.duration)

        // The Hall.toDB() -> HallDB mapping is tested in the HallMapperTest.kt file
        assertEquals(slot.halls.map(Hall::toDB).toSet(), slotDB.halls)

        // The Session.toDB() -> SessionDB mapping
        // and the ManualSession.toDB() -> ManualSessionDB mapping
        // are tested in the SessionMapper.kt file
        // We don't use a limited mapping, as object sent to the API are finite
        // and should then not have infinite recursive objects
        assertEquals(slot.session?.toDB(), slotDB.session)
        assertEquals(slot.manualSession?.toDB(), slotDB.manualSession)

        // The Event.toDB() -> EventDB mapping is tested in the EventMapperTest.kt file
        assertEquals(slot.event.toDB(), slotDB.event)

        assertEquals(slot.barcode, slotDB.barcode)
        assertEquals(slot.title, slotDB.title)
    }
}