package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.exceptions.HallNotFoundException
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.breizhcamp.konter.domain.use_cases.ports.KalonPort
import org.breizhcamp.konter.testUtils.HallGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(HallAssociateEvent::class)
class HallAssociateEventTest {

    @MockkBean
    private lateinit var hallPort: HallPort

    @MockkBean
    private lateinit var eventPort: EventPort

    @MockkBean
    private lateinit var kalonPort: KalonPort


    @Autowired
    private lateinit var hallAssociateEvent: HallAssociateEvent

    private lateinit var hall: Hall
    private var eventId: Int = 0
    private var order: Int = 0

    @BeforeEach
    fun setUp() {
        hall = HallGen().generateOne()
        eventId = Random.nextInt().absoluteValue
        order = Random.nextInt().absoluteValue

        every { hallPort.associateToEvent(hall.id, eventId, order) } returns hall
    }

    @Test
    fun `associate should call eventPort and throw if the Event is not found after reloading data from Kalon`() {
        every { eventPort.existsById(eventId) } returns false
        every { kalonPort.getEvents() } returns emptyList()
        every { eventPort.save(emptyList()) } just Runs

        assertThrows<HallNotFoundException> { hallAssociateEvent.associate(hall.id, eventId, order) }

        verify(exactly = 2) { eventPort.existsById(eventId) }
        verify { kalonPort.getEvents() }
        verify { eventPort.save(emptyList()) }
        verify { hallPort.associateToEvent(hall.id, eventId, order) wasNot Called }
    }

    @Test
    fun `associate should call eventPort and continue as expected if the Event was found in Kalon`() {
        every { eventPort.existsById(eventId) } returnsMany listOf(false, true)
        every { kalonPort.getEvents() } returns emptyList()
        every { eventPort.save(emptyList()) } just Runs

        assertEquals(hall, hallAssociateEvent.associate(hall.id, eventId, order))

        verify(exactly = 2) { eventPort.existsById(eventId) }
        verify { kalonPort.getEvents() }
        verify { eventPort.save(emptyList()) }
        verify { hallPort.associateToEvent(hall.id, eventId, order) }
    }

    @Test
    fun `associate should call Port with its inputs and return the result`() {
        every { eventPort.existsById(eventId) } returns true

        assertEquals(hall, hallAssociateEvent.associate(hall.id, eventId, order))

        verify(exactly = 1) { eventPort.existsById(eventId) }
        verify { hallPort.associateToEvent(hall.id, eventId, order) }
    }

    @Test
    fun `dissociate should call Port with its inputs and return the result`() {
        every { hallPort.dissociateFromEvent(hall.id, eventId) } returns hall

        assertEquals(hall, hallAssociateEvent.dissociate(hall.id, eventId))

        verify { hallPort.dissociateFromEvent(hall.id, eventId) }
    }
}