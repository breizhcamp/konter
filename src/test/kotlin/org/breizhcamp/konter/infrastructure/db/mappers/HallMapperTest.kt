package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.testUtils.HallDBGen
import org.breizhcamp.konter.testUtils.HallGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HallMapperTest {

    @Test
    fun `toHall should convert all fields one to one`() {
        val hallDB = HallDBGen().generateOne()
        val hall = hallDB.toHall()

        assertEquals(hallDB.id,         hall.id)
        assertEquals(hallDB.name,       hall.name)
        assertEquals(hallDB.trackId,    hall.trackId)
    }

    @Test
    fun `toDB should convert all fields one to one`() {
        val hall = HallGen().generateOne()
        val hallDB = hall.toDB()

        assertEquals(hall.id,       hallDB.id)
        assertEquals(hall.name,     hallDB.name)
        assertEquals(hall.trackId,  hallDB.trackId)
    }
}