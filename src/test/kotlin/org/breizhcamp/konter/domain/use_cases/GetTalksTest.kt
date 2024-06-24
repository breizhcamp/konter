package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.domain.entities.Talk
import org.breizhcamp.konter.domain.entities.exceptions.EventNoBeginException
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.domain.use_cases.ports.KalonPort
import org.breizhcamp.konter.testUtils.EventGen
import org.breizhcamp.konter.testUtils.TalkGen
import org.breizhcamp.konter.testUtils.generateRandomLocalDateTime
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
@WebMvcTest(GetTalks::class)
class GetTalksTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @MockkBean
    private lateinit var kalonPort: KalonPort

    @Autowired
    private lateinit var getTalks: GetTalks

    private lateinit var talks: List<Talk>
    private var eventId = 0

    @BeforeEach
    fun setUp() {
        talks = TalkGen().generateList()
        eventId = Random.nextInt().absoluteValue

        every { kalonPort.getEvents() } returns emptyList()
        every { eventPort.save(any<List<Event>>()) } just Runs
        every { eventPort.exportTalks(eventId) } returns talks
    }

    @Test
    fun `list should call kalon if the event has no begin date, and throw if it still has none after reload`() {
        val event = EventGen().generateOne().copy(id = eventId, begin = null)
        every { eventPort.getById(eventId) } returns event

        assertThrows<EventNoBeginException> { getTalks.list(event.id) }

        verify(exactly = 2) { eventPort.getById(event.id) }
        verify { eventPort.save(any<List<Event>>()) }
        verify { kalonPort.getEvents() }
        verify(exactly = 0) { eventPort.exportTalks(event.id) }
    }

    @Test
    fun `list should call kalon if the event has no begin date and proceed if it has one after reload`() {
        val eventNoBegin = EventGen()
            .generateOne()
            .copy(id = eventId, begin = null)
        val eventSetBegin = eventNoBegin.copy(
            id = eventId,
            begin = generateRandomLocalDateTime().toLocalDate()
        )

        every { eventPort.getById(eventId) } returnsMany listOf(eventNoBegin, eventSetBegin)

        assertEquals(talks, getTalks.list(eventNoBegin.id))

        verify(exactly = 2) { eventPort.getById(eventId) }
        verify { eventPort.save(any<List<Event>>()) }
        verify { kalonPort.getEvents() }
        verify { eventPort.exportTalks(eventId) }
    }

    @Test
    fun `list should not call kalon if the event has a begin date, and call eventPort to export the talks`() {
        val event = EventGen()
            .generateOne()
            .copy(id = eventId, begin = generateRandomLocalDateTime().toLocalDate())
        every { eventPort.getById(eventId) } returns event

        assertEquals(talks, getTalks.list(eventId))

        verify(exactly = 1) { eventPort.getById(eventId) }
        verify(exactly = 0) { eventPort.save(any<List<Event>>()) }
        verify(exactly = 0) { kalonPort.getEvents() }
        verify { eventPort.exportTalks(eventId) }
    }
}