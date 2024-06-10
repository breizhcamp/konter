package org.breizhcamp.konter.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.application.requests.SlotCreationReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.Slot
import org.breizhcamp.konter.domain.entities.exceptions.HallNotFoundException
import org.breizhcamp.konter.domain.entities.exceptions.TimeConflictException
import org.breizhcamp.konter.domain.use_cases.EventGet
import org.breizhcamp.konter.domain.use_cases.SlotAssociateHall
import org.breizhcamp.konter.domain.use_cases.SlotCRUD
import org.breizhcamp.konter.domain.use_cases.SlotGenerateProgram
import org.breizhcamp.konter.testUtils.EventGen
import org.breizhcamp.konter.testUtils.HallGen
import org.breizhcamp.konter.testUtils.SlotGen
import org.breizhcamp.konter.testUtils.generateRandomHexString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SlotController::class)
class SlotControllerTest {

    @MockkBean
    private lateinit var slotCrud: SlotCRUD

    @MockkBean
    private lateinit var eventGet: EventGet

    @MockkBean
    private lateinit var slotGenerateProgram: SlotGenerateProgram

    @MockkBean
    private lateinit var slotAssociateHall: SlotAssociateHall

    @Autowired
    private lateinit var slotController: SlotController

    @Test
    fun `listSlotForEvent should log, call CRUD and map the result to change Halls to their Ids and Slots to DTOs`(output: CapturedOutput) {
        val day = Random.nextInt().absoluteValue
        val hall = HallGen().generateOne()
        val slots = SlotGen().generateList()
        val program: Map<Int, Map<Hall, List<Slot>>> =
            mapOf(
                Pair(day, mapOf(
                    Pair(
                        hall, slots
                    )
                ))
            )
        val eventId = Random.nextInt().absoluteValue

        every { slotCrud.list(eventId) } returns program

        val result = slotController.listSlotForEvent(eventId)
        assert(output.contains("Listing all slots in Event:$eventId grouping by Hall"))

        assert(result.containsKey(day))
        assert(result[day]!!.containsKey(hall.id))
        assertEquals(slots.map(Slot::toDto), result[day]!![hall.id])

        verify { slotCrud.list(eventId) }
    }

    enum class Scenarios {
        NO_EXCEPTION,
        TIME_CONFLICT,
        NOT_FOUND
    }

    @Nested
    inner class CRUTests {
        private lateinit var slot: Slot

        @BeforeEach
        fun setUp() {
            slot = SlotGen().generateOne()
        }

        @ParameterizedTest
        @EnumSource(Scenarios::class)
        fun `addSlotToHall should log, call CRUD return a CONFLICT on TimeConflictException, NOT_FOUND on HallNotFoundException and the result as a DTO on no Exception`(scenario: Scenarios, output: CapturedOutput) {
            val hallId = Random.nextInt().absoluteValue
            val eventId = Random.nextInt().absoluteValue
            val request = SlotCreationReq(slot.start, slot.day, slot.duration)

            val message = generateRandomHexString()

            when(scenario) {
                Scenarios.NO_EXCEPTION -> every { slotCrud.create(hallId, eventId, request) } returns slot
                Scenarios.TIME_CONFLICT -> every { slotCrud.create(hallId, eventId, request) } throws TimeConflictException(message)
                Scenarios.NOT_FOUND -> every { slotCrud.create(hallId, eventId, request) } throws HallNotFoundException(message)
            }

            val response = slotController.addSlotToHall(eventId, hallId, request)
            assert(output.contains("Adding slot to Hall:$hallId in Event:$eventId"))
            when(scenario) {
                Scenarios.NO_EXCEPTION -> {
                    assertEquals(slot.toDto(), response.body)
                }
                Scenarios.TIME_CONFLICT -> {
                    assertEquals(HttpStatus.CONFLICT, response.statusCode)
                    assertEquals(message, response.body)
                    assert(output.contains(message))
                }
                Scenarios.NOT_FOUND -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
                    assertEquals(message, response.body)
                    assert(output.contains(message))
                }
            }

            verify { slotCrud.create(hallId, eventId, request) }
        }

        @Test
        fun `getSlot should log, call CRUD and return the result as a DTO`(output: CapturedOutput) {
            every { slotCrud.get(slot.id) } returns slot

            assertEquals(slot.toDto(), slotController.getSlot(slot.id))
            assert(output.contains("Retrieving Slot:${slot.id}"))

            verify { slotCrud.get(slot.id) }
        }

        @Nested
        inner class  HallAssignmentTests {
            private var eventId: Int = 0
            private var hallId: Int = 0

            @BeforeEach
            fun setUp() {
                eventId = Random.nextInt().absoluteValue
                hallId = Random.nextInt().absoluteValue
            }

            @Test
            fun `assignHallToSlot should log, call AssociateHall and return the result as a DTO`(output: CapturedOutput) {
                every { slotAssociateHall.associate(slot.id, eventId, hallId) } returns slot

                assertEquals(slot.toDto(), slotController.assignHallToSlot(slot.id, eventId, hallId))
                assert(output.contains("Assigning Hall:$hallId to Slot:${slot.id} in Event:$eventId"))

                verify { slotAssociateHall.associate(slot.id, eventId, hallId) }
            }

            @Test
            fun `resignHallFromSlot should log, call AssociateHall and return the result as a DTO`(output: CapturedOutput) {
                every { slotAssociateHall.dissociate(slot.id, hallId) } returns slot

                assertEquals(slot.toDto(), slotController.resignHallFromSlot(slot.id, hallId))
                assert(output.contains("Resigning Hall:$hallId from Slot:${slot.id}"))

                verify { slotAssociateHall.dissociate(slot.id, hallId) }
            }
        }
    }

    @Test
    fun `deleteSlot should log and call CRUD`(output: CapturedOutput) {
        val id = UUID.randomUUID()
        every { slotCrud.delete(id) } just Runs

        slotController.deleteSlot(id)
        assert(output.contains("Deleting Slot:$id"))

        verify { slotCrud.delete(id) }
    }

    @Test
    fun `exportProgram should log, call EventGet, set the correct HTTP headers and pass the response to GenerateProgram`(output: CapturedOutput) {
        val event = EventGen().generateOne()
        val contentDisposition = "attachment; filename=\"program_${event.name}.pdf\""
        val response = MockHttpServletResponse()

        every { eventGet.getById(event.id) } returns event
        every { slotGenerateProgram.generateEmptyProgramPdf(event.id, response.outputStream) } just Runs

        slotController.exportProgram(event.id, response)
        assert(output.contains("Generating program for Event:${event.id}"))

        assertEquals(contentDisposition, response.getHeader(HttpHeaders.CONTENT_DISPOSITION))
        assertEquals(MediaType.APPLICATION_PDF_VALUE, response.getHeader(HttpHeaders.CONTENT_TYPE))

        verify { eventGet.getById(event.id) }
        verify { slotGenerateProgram.generateEmptyProgramPdf(event.id, response.outputStream) }
    }
}