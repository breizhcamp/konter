package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.breizhcamp.konter.infrastructure.db.mappers.toEvent
import org.breizhcamp.konter.infrastructure.db.model.EventDB
import org.breizhcamp.konter.infrastructure.db.repos.EventRepo
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.breizhcamp.konter.testUtils.EventDBGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventAdapter::class)
class EventAdapterTest {

    @MockkBean
    private lateinit var eventRepo: EventRepo

    @MockkBean
    private lateinit var hallRepo: HallRepo

    @Autowired
    private lateinit var eventAdapter: EventAdapter

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `existsById should call repo and return the result`(exists: Boolean) {
        val id = Random.nextInt().absoluteValue
        every { eventRepo.existsById(id) } returns exists

        assertEquals(exists, eventAdapter.existsById(id))

        verify { eventRepo.existsById(id) }
    }

    @Nested
    inner class CreateReadTests {
        private lateinit var event: EventDB

        @BeforeEach
        fun setUp() {
            event = EventDBGen().generateOne()
        }

        @ParameterizedTest
        @ValueSource(booleans = [false, true])
        fun `getById should call repo, return the result if the event was found and throw if not`(
            exists: Boolean
        ) {
            every { eventRepo.findById(event.id) } returns
                    if (exists) Optional.of(event)
                    else Optional.empty()

            if (exists) {
                assertEquals(event.toEvent(), eventAdapter.getById(event.id))
            } else {
                assertThrows<NoSuchElementException> { eventAdapter.getById(event.id) }
            }

            verify { eventRepo.findById(event.id) }
        }

        @ParameterizedTest
        @ValueSource(booleans = [false, true])
        fun `save should call repo`(exists: Boolean) {
            every { eventRepo.findById(event.id) } returns
                    if (exists) Optional.of(event)
                    else Optional.empty()
            every { eventRepo.save(event) } returns event

            eventAdapter.save(event.toEvent())

            verify { eventRepo.findById(event.id) }
            verify { eventRepo.save(event) }
        }
    }

    @Test
    fun `listSave should call repo for each element`() {
        val events = EventDBGen().generateList()
        val eventDBSlot = slot<EventDB>()
        every { eventRepo.save(capture(eventDBSlot)) } answers {
            eventDBSlot.captured
        }
        every { eventRepo.findById(any()) } returns Optional.empty()

        eventAdapter.save(events.map { it.toEvent() })

        verify(exactly = events.size) { eventRepo.save(any()) }
        verify(exactly = events.size) { eventRepo.findById(any()) }
    }
}