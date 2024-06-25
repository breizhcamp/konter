package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.breizhcamp.konter.domain.entities.exceptions.HallNotFoundException
import org.breizhcamp.konter.infrastructure.db.mappers.toEvent
import org.breizhcamp.konter.infrastructure.db.mappers.toHall
import org.breizhcamp.konter.infrastructure.db.mappers.toSpeaker
import org.breizhcamp.konter.infrastructure.db.model.EventDB
import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.breizhcamp.konter.infrastructure.db.model.SlotDB
import org.breizhcamp.konter.infrastructure.db.repos.EventRepo
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.breizhcamp.konter.testUtils.EventDBGen
import org.breizhcamp.konter.testUtils.HallDBGen
import org.breizhcamp.konter.testUtils.ImportSlotDBGen
import org.breizhcamp.konter.testUtils.ManualSlotDBGen
import org.junit.jupiter.api.Assertions.*
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
import java.time.temporal.ChronoUnit
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

    @Test
    fun `exportTalks should call repos and join manual sessions and sessions as Talks, then return it`() {
        val halls = HallDBGen().generateList()
        val event = EventDBGen().generateOne().copy(halls = halls.toSet())
        val manualSlots = ManualSlotDBGen()
            .generateList()
            .map { it.copy(halls = halls.toSet()) }
        val manualTalks = manualSlots.map { it.toManualTalk(event, halls) }
        val normalSlots = ImportSlotDBGen()
            .generateList()
            .map { it.copy(halls = halls.toSet()) }
        val normalTalks = normalSlots.map { it.toImportTalk(event, halls) }
        val slots = manualSlots + normalSlots
        val talks = manualTalks + normalTalks

        every { eventRepo.findById(event.id) } returns Optional.of(event)
        every { hallRepo.getAllByAvailableEventId(event.id) } returns halls
        every { eventRepo.listSlotsHeld(event.id) } returns slots

        assertEquals(talks, eventAdapter.exportTalks(event.id))

        verify { eventRepo.findById(event.id) }
        verify { hallRepo.getAllByAvailableEventId(event.id) }
        verify { eventRepo.listSlotsHeld(event.id) }
    }

    @Nested
    inner class TalkMapping {

        private lateinit var event: EventDB
        private lateinit var halls: List<HallDB>

        private lateinit var manualSlot: SlotDB
        private lateinit var importedSlot: SlotDB


        @BeforeEach
        fun setUp() {
            event = EventDBGen().generateOne()
            halls = HallDBGen().generateList()

            manualSlot = ManualSlotDBGen().generateOne().copy(halls = halls.toSet())
            importedSlot = ImportSlotDBGen().generateOne().copy(halls = halls.toSet())
        }

        @Test
        fun `computeDate should throw if event begin is null`() {
            val nullBeginEvent = event.copy(begin = null)
            assertThrows<IllegalArgumentException> { manualSlot.computeDate(nullBeginEvent) }
        }

        @Test
        fun `computeDate should return the date of the start of the slot based on the begin date of the event`() {
            assertNotNull(event.begin)
            val expectedDate = requireNotNull(event.begin)
                .plus((manualSlot.day - 1).toLong(), ChronoUnit.DAYS)

            assertEquals(expectedDate, manualSlot.computeDate(event))
        }

        @Test
        fun `toManualTalk should throw if the slot has a null manualSession`() {
            assertThrows<IllegalArgumentException> { importedSlot.toManualTalk(event, halls) }
        }

        @Test
        fun `toManualTalk should throw if slot halls have an empty intersection with availableHalls`() {
            assertThrows<HallNotFoundException> { manualSlot.toManualTalk(event, emptyList()) }
        }

        @Test
        fun `toManualTalk should use the manualSession values`() {
            val date = manualSlot.computeDate(event)

            assertNotNull(manualSlot.manualSession)
            val manualSession = requireNotNull(manualSlot.manualSession)

            val talk = manualSlot.toManualTalk(event, halls)

            assertEquals(manualSession.id, talk.id)
            assertEquals(manualSession.title, talk.name)
            assertEquals(manualSession.theme, talk.eventType)
            assertEquals(manualSession.format, talk.format)
            assertEquals(manualSession.description, talk.description)

            assertEquals(manualSlot.start.atDate(date), talk.eventStart)
            assertEquals(
                manualSlot.start.plus(manualSlot.duration).atDate(date),
                talk.eventEnd
            )

            assertEquals(manualSession.speakers.map { it.toSpeaker() }, talk.speakers)
            assertEquals(manualSlot.halls.first().toHall(), talk.hall)

            assertNull(talk.videoUrl)
            assertNull(talk.filesUrl)
            assertNull(talk.slidesUrl)
        }

        @Test
        fun `toImportTalk should throw if the slot has a null session`() {
            assertThrows<IllegalArgumentException> { manualSlot.toImportTalk(event, halls) }
        }

        @Test
        fun `toImportTalk should throw if slot halls have an empty intersection with availableHalls`() {
            assertThrows<HallNotFoundException> { importedSlot.toImportTalk(event, emptyList()) }
        }

        @Test
        fun `toImportTalk should use the session values`() {
            val date = importedSlot.computeDate(event)

            assertNotNull(importedSlot.session)
            val session = requireNotNull(importedSlot.session)

            val talk = importedSlot.toImportTalk(event, halls)

            assertEquals(session.id, talk.id)
            assertEquals(session.title, talk.name)
            assertEquals(session.theme, talk.eventType)
            assertEquals(session.format, talk.format)
            assertEquals(session.description, talk.description)

            assertEquals(importedSlot.start.atDate(date), talk.eventStart)
            assertEquals(
                importedSlot.start.plus(importedSlot.duration).atDate(date),
                talk.eventEnd
            )

            assertEquals(session.speakers.map { it.toSpeaker() }, talk.speakers)
            assertEquals(importedSlot.halls.first().toHall(), talk.hall)

            assertNull(talk.videoUrl)
            assertNull(talk.filesUrl)
            assertNull(talk.slidesUrl)
        }
    }
}