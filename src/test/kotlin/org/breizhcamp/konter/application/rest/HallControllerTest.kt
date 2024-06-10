package org.breizhcamp.konter.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.application.requests.HallPatchReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.HallAssociateEvent
import org.breizhcamp.konter.domain.use_cases.HallCRUD
import org.breizhcamp.konter.domain.use_cases.HallSetOrder
import org.breizhcamp.konter.testUtils.HallGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(HallController::class)
class HallControllerTest {

    @MockkBean
    private lateinit var hallCRUD: HallCRUD

    @MockkBean
    private lateinit var hallAssociateEvent: HallAssociateEvent

    @MockkBean
    private lateinit var hallSetOrder: HallSetOrder

    @Autowired
    private lateinit var hallController: HallController

    @Nested
    inner class ListTests {
        private lateinit var halls: List<Hall>

        @BeforeEach
        fun setUp() {
            halls = HallGen().generateList()
        }

        @Test
        fun `listAll should log, call CRUD and return the result as a List of DTOs`(output: CapturedOutput) {
            every { hallCRUD.listAll() } returns halls

            assertEquals(halls.map(Hall::toDto), hallController.listAll())
            assert(output.contains("Listing all Halls"))

            verify { hallCRUD.listAll() }
        }

        @Test
        fun `listByEvent should log, call CRUD with its input and return the result as a List of DTOs`(output: CapturedOutput) {
            val eventId = Random.nextInt().absoluteValue
            every { hallCRUD.listByEvent(eventId) } returns halls

            assertEquals(halls.map(Hall::toDto), hallController.listByEvent(eventId))
            assert(output.contains("Listing Halls available for Event:$eventId"))

            verify { hallCRUD.listByEvent(eventId) }
        }

    }

    @Nested
    inner class CreateAndPatchTests {
        private lateinit var hall: Hall

        @BeforeEach
        fun setUp() {
            hall = HallGen().generateOne()
        }

        @Test
        fun `createHall should log, call CRUD with its input and return the result as a DTO`(output: CapturedOutput) {
            val request = HallCreationReq(name = hall.name!!, trackId = hall.trackId)
            every { hallCRUD.create(request) } returns hall

            assertEquals(hall.toDto(), hallController.createHall(request))
            assert(output.contains(
                "Creating a Hall with name ${request.name} and trackId ${request.trackId}"
            ))

            verify { hallCRUD.create(request) }
        }

        @Test
        fun `patchHall should log, call CRUD with its input and return the result as a DTO`(output: CapturedOutput) {
            val request = HallPatchReq(name = hall.name!!, trackId = hall.trackId)
            every { hallCRUD.update(hall.id, request) } returns hall

            assertEquals(hall.toDto(), hallController.patchHall(hall.id, request))
            assert(output.contains("Updating Hall:${hall.id}"))

            verify { hallCRUD.update(hall.id, request) }
        }
    }

    @Test
    fun `deleteHall should log and call CRUD with its input`(output: CapturedOutput) {
        val id = Random.nextInt().absoluteValue
        every { hallCRUD.delete(id) } just Runs

        hallController.deleteHall(id)
        assert(output.contains("Deleting Hall:$id"))

        verify { hallCRUD.delete(id) }
    }

    @Nested
    inner class EventRelativeTests {
        private var eventId: Int = 0
        private lateinit var hall: Hall

        @BeforeEach
        fun setUp() {
            eventId = Random.nextInt().absoluteValue
            hall = HallGen().generateOne()
        }

        @Test
        fun `associateToEvent should log, call AssociateEvent with its inputs and return the result as a DTO`(output: CapturedOutput) {
            every { hallAssociateEvent.associate(hall.id, eventId) } returns hall

            assertEquals(hall.toDto(), hallController.associateToEvent(hall.id, eventId))
            assert(output.contains("Associating Hall:${hall.id} to Event:$eventId"))

            verify { hallAssociateEvent.associate(hall.id, eventId) }
        }

        @Test
        fun `dissociateFromEvent should log, call AssociateEvent with its inputs and return the result as a DTO`(output: CapturedOutput) {
            every { hallAssociateEvent.dissociate(hall.id, eventId) } returns hall

            assertEquals(hall.toDto(), hallController.dissociateFromEvent(hall.id, eventId))
            assert(output.contains("Dissociating Hall:${hall.id} from Event:$eventId"))

            verify { hallAssociateEvent.dissociate(hall.id, eventId) }
        }

        @Test
        fun `updateOrderForEvent should log, call SetOrderEvent with its inputs and return the result as a DTO`(output: CapturedOutput) {
            val order = Random.nextInt().absoluteValue
            every { hallSetOrder.setOrder(hall.id, eventId, order) } returns hall

            assertEquals(hall.toDto(), hallController.updateOrderForEvent(hall.id, eventId, order))
            assert(output.contains("Updating order of Hall:${hall.id} for Event:$eventId to $order"))

            verify { hallSetOrder.setOrder(hall.id, eventId, order) }
        }
    }
}