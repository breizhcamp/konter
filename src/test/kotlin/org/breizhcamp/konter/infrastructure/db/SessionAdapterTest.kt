package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.domain.entities.Evaluation
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.infrastructure.db.mappers.toDB
import org.breizhcamp.konter.infrastructure.db.mappers.toSession
import org.breizhcamp.konter.infrastructure.db.model.SessionDB
import org.breizhcamp.konter.infrastructure.db.repos.SessionRepo
import org.breizhcamp.konter.testUtils.SessionDBGen
import org.breizhcamp.konter.testUtils.SessionFilterGen
import org.breizhcamp.konter.testUtils.SessionGen
import org.breizhcamp.konter.testUtils.generateRandomHexString
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(SessionAdapter::class)
class SessionAdapterTest {

    @MockkBean
    private lateinit var sessionRepo: SessionRepo

    @Autowired
    private lateinit var sessionAdapter: SessionAdapter

    @Test
    fun `getById should throw if the session does not exist`() {
        val id = Random.nextInt().absoluteValue
        every { sessionRepo.findById(id) } returns Optional.empty()

        assertThrows<NoSuchElementException> { sessionAdapter.getById(id) }

        verify { sessionRepo.findById(id) }
    }

    @Test
    fun `getById should return the result as a session if the session exists`() {
        val session = SessionDBGen().generateOne()
        every { sessionRepo.findById(session.id) } returns Optional.of(session)

        assertEquals(session.toSession(), sessionAdapter.getById(session.id))

        verify { sessionRepo.findById(session.id) }
    }

    @Test
    fun `import should update existing session if it was found`() {
        val sessionDB = SessionDBGen().generateOne()
        val session = SessionGen().generateOne()
        every { sessionRepo.existsById(session.id) } returns true
        every { sessionRepo.findById(session.id) } returns Optional.of(sessionDB)

        val sessionAfter = session.toDB().copy(
            slot = sessionDB.slot,
            videoURL = sessionDB.videoURL,
            barcode = sessionDB.barcode
        )
        every { sessionRepo.save(sessionAfter) } returns sessionAfter

        sessionAdapter.import(session)

        verify { sessionRepo.existsById(session.id) }
        verify { sessionRepo.findById(session.id) }
        verify { sessionRepo.save(sessionAfter) }
    }

    @Test
    fun `import should save values if no session with the same id was found`() {
        val session = SessionGen().generateOne()
        val sessionDB = session.toDB()

        every { sessionRepo.existsById(session.id) } returns false
        every { sessionRepo.findById(session.id) } returns Optional.empty()
        every { sessionRepo.save(sessionDB) } returns sessionDB

        sessionAdapter.import(session)

        verify { sessionRepo.existsById(session.id) }
        verify(exactly = 0) { sessionRepo.findById(session.id) }
        verify { sessionRepo.save(sessionDB) }
    }

    @Test
    fun `getAllByEventId should call repo with its inputs and an empty filter, and return the result as a List of Session`() {
        val eventId = Random.nextInt().absoluteValue
        val sortByFormat = Random.nextBoolean()
        val result = SessionDBGen().generateList()

        every { sessionRepo.filter(eventId, SessionFilter.empty(), sortByFormat) } returns result

        assertEquals(result.map { it.toSession() }, sessionAdapter.getAllByEventId(eventId, sortByFormat))

        verify { sessionRepo.filter(eventId, SessionFilter.empty(), sortByFormat) }
    }

    @Test
    fun `saveEvaluation should set the rating for the session in the evaluation and save it`() {
        val session = SessionGen().generateOne()
        val evaluation = Evaluation(
            session = session,
            rating = BigDecimal.valueOf(Random.nextDouble(0.0, 5.0))
        )
        val sessionDB = session.copy(rating = evaluation.rating).toDB()

        every { sessionRepo.save(sessionDB) } returns sessionDB

        sessionAdapter.saveEvaluation(evaluation)

        verify { sessionRepo.save(sessionDB) }
    }

    @Test
    fun `filterByEventId should call repo with its input and sortByFormat set to false, and return the result as a List of Session`() {
        val filter = SessionFilterGen().generateOne()
        val eventId = Random.nextInt().absoluteValue
        val result = SessionDBGen().generateList()

        every { sessionRepo.filter(eventId, filter, false) } returns result

        assertEquals(result.map { it.toSession() }, sessionAdapter.filterByEventId(eventId, filter))

        verify { sessionRepo.filter(eventId, filter, false) }
    }

    @Test
    fun `addBarcode should call repo with its inputs`() {
        val id = Random.nextInt().absoluteValue
        val barcode = generateRandomHexString()
        every { sessionRepo.addBarcode(id, barcode) } just Runs

        sessionAdapter.addBarcode(id, barcode)

        verify { sessionRepo.addBarcode(id, barcode) }
    }

    @Nested
    inner class SetSlotTests {
        private lateinit var session: SessionDB
        private lateinit var slotId: UUID

        @BeforeEach
        fun setUp() {
            session = SessionDBGen().generateOne()
            slotId = UUID.randomUUID()

            every { sessionRepo.removeSlotById(session.id) } just Runs
            every { sessionRepo.setSlotById(session.id, slotId) } just Runs
            every { sessionRepo.findById(session.id) } returns Optional.of(session)
        }

        @AfterEach
        fun breakDown() {
            verify { sessionRepo.hasSlotById(session.id) }
            verify { sessionRepo.setSlotById(session.id, slotId) }
            verify { sessionRepo.findById(session.id) }
        }

        @Test
        fun `setSlotById should reset the slot before assigning one if the session already is part of a slot, and return the result as a Session`() {
            every { sessionRepo.hasSlotById(session.id) } returns true

            assertEquals(
                session.toSession(),
                sessionAdapter.setSlotById(session.id, slotId)
            )

            verify { sessionRepo.removeSlotById(session.id) }
        }

        @Test
        fun `setSlotById should not reset the slot before assigning one if the session is not part of a slot, and return the result as a Session`() {
            every { sessionRepo.hasSlotById(session.id) } returns false

            assertEquals(
                session.toSession(),
                sessionAdapter.setSlotById(session.id, slotId)
            )

            verify(exactly = 0) { sessionRepo.removeSlotById(session.id) }
        }

        @Test
        fun `setSlotByBarcode should reset the slot before assigning one if the session already is part of a slot, and return the result as a Session`() {
            val barcode = generateRandomHexString()
            every { sessionRepo.getSlotIdByBarcode(barcode) } returns slotId
            every { sessionRepo.hasSlotById(session.id) } returns true

            assertEquals(
                session.toSession(),
                sessionAdapter.setSlotByBarcode(session.id, barcode)
            )

            verify { sessionRepo.getSlotIdByBarcode(barcode) }
            verify { sessionRepo.removeSlotById(session.id) }
        }

        @Test
        fun `setSlotByBarcode should not reset the slot before assigning one if the session is not part of a slot, and return the result as a Session`() {
            val barcode = generateRandomHexString()
            every { sessionRepo.getSlotIdByBarcode(barcode) } returns slotId
            every { sessionRepo.hasSlotById(session.id) } returns false

            assertEquals(
                session.toSession(),
                sessionAdapter.setSlotByBarcode(session.id, barcode)
            )

            verify { sessionRepo.getSlotIdByBarcode(barcode) }
            verify(exactly = 0) { sessionRepo.removeSlotById(session.id) }
        }
    }
}