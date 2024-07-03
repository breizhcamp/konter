package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.breizhcamp.konter.testUtils.EventDBGen
import org.breizhcamp.konter.testUtils.EventGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventMapperTest {

    @Test
    fun `toEvent should convert all fields one to one except the halls`() {
        val eventDB = EventDBGen().generateOne()
        val event = eventDB.toEvent()

        assertEquals(eventDB.id,    event.id)
        assertEquals(eventDB.year,  event.year)
        assertEquals(eventDB.name,  event.name)
    }

    @Test
    fun `toDB should convert all fields one to one and set the halls to an empty Set`() {
        val event = EventGen().generateOne()
        val eventDB = event.toDB()
        val emptyHallSet = emptySet<HallDB>()

        assertEquals(event.id,      eventDB.id)
        assertEquals(event.year,    eventDB.year)
        assertEquals(event.name,    eventDB.name)
        assertEquals(emptyHallSet,  eventDB.halls)
    }
}