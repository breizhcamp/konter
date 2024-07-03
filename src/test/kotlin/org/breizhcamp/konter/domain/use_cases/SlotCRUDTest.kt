package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.application.requests.SlotCreationReq
import org.breizhcamp.konter.application.requests.SlotPatchReq
import org.breizhcamp.konter.domain.entities.Slot
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.breizhcamp.konter.testUtils.HallGen
import org.breizhcamp.konter.testUtils.SlotGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(SlotCRUD::class)
class SlotCRUDTest {

    @MockkBean
    private lateinit var slotPort: SlotPort

    @Autowired
    private lateinit var slotCRUD: SlotCRUD

    @Nested
    inner class CRUTests {
        private lateinit var slot: Slot

        @BeforeEach
        fun setUp() {
            slot = SlotGen().generateOne()
        }

        @Test
        fun `create should call Port with its inputs and return the result`() {
            val hallId = Random.nextInt().absoluteValue
            val eventId = Random.nextInt().absoluteValue
            val req = SlotCreationReq(slot.start, slot.day, slot.duration, slot.title, listOf(hallId), slot.assignable)

            every { slotPort.create(eventId, req) } returns slot

            assertEquals(slot, slotCRUD.create(eventId, req))

            verify { slotPort.create(eventId, req) }
        }

        @Test
        fun `get should call Port with its input and return the result`() {
            every { slotPort.getById(slot.id) } returns slot

            assertEquals(slot, slotCRUD.get(slot.id))

            verify { slotPort.getById(slot.id) }
        }

        @Test
        fun `update should call Port with its inputs and return the result`() {
            val req = SlotPatchReq(slot.title, slot.assignable)
            every { slotPort.update(slot.id, req) } returns slot

            assertEquals(slot, slotCRUD.update(slot.id, req))

            verify { slotPort.update(slot.id, req) }
        }
    }

    @Test
    fun `list should call Port with its input and return the result`() {
        val day = Random.nextInt().absoluteValue
        val hall = HallGen().generateOne()
        val slots = SlotGen().generateList()
        val program = mapOf(
            Pair(day, mapOf(
                Pair(hall, slots)
            ))
        )
        val eventId = Random.nextInt().absoluteValue

        every { slotPort.getProgram(eventId) } returns program

        assertEquals(program, slotCRUD.list(eventId))

        verify { slotPort.getProgram(eventId) }
    }

    @Test
    fun `delete should call Port with its input`() {
        val id = UUID.randomUUID()
        every { slotPort.remove(id) } just Runs

        slotCRUD.delete(id)

        verify { slotPort.remove(id) }
    }
}