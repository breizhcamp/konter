package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.application.requests.HallPatchReq
import org.breizhcamp.konter.infrastructure.db.mappers.toHall
import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.breizhcamp.konter.testUtils.HallDBGen
import org.breizhcamp.konter.testUtils.generateRandomHexString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(HallAdapter::class)
class HallAdapterTest {

    @MockkBean
    private lateinit var hallRepo: HallRepo

    @Autowired
    private lateinit var hallAdapter: HallAdapter

    enum class UpdateCases {
        NoUpdate,
        UpdateName,
        UpdateTrackId
    }

    @Nested
    inner class ListTests {
        private lateinit var halls: List<HallDB>

        @BeforeEach
        fun setUp() {
            halls = HallDBGen().generateList()
        }

        @Test
        fun `list with null eventId should call repo to retrieve all HallDBs regardless of event, and return them as a List of Hall`() {
            every { hallRepo.findAll() } returns halls

            assertEquals(halls.map { it.toHall() }, hallAdapter.list(null))

            verify { hallRepo.findAll() }
        }

        @Test
        fun `list with non null eventId should call repo to retrieve HallDBs that are available in the corresponding event, and return them as a List of Hall`() {
            val eventId = Random.nextInt().absoluteValue
            every { hallRepo.getAllByAvailableEventId(eventId) } returns halls

            assertEquals(halls.map { it.toHall() }, hallAdapter.list(eventId))

            verify { hallRepo.getAllByAvailableEventId(eventId) }
        }
    }

    @Nested
    inner class CreateTests {
        private lateinit var hall: HallDB
        private lateinit var req: HallCreationReq

        @BeforeEach
        fun setUp() {
            hall = HallDBGen().generateOne()
            req = HallCreationReq(requireNotNull(hall.name), null)

            every { hallRepo.findById(hall.id) } returns Optional.of(hall)
            every { hallRepo.create(req.name) } returns hall.id
        }

        @AfterEach
        fun breakDown() {
            verify { hallRepo.findById(hall.id) }
            verify { hallRepo.create(req.name) }
        }

        @Test
        fun `create should call repo to create, only retrieve the hall if the trackId is null and return it as a Hall`() {
            every { hallRepo.addTrackIdToHall(any(), any()) } just Runs

            assertEquals(hall.toHall(), hallAdapter.create(req))

            verify(exactly = 0) { hallRepo.addTrackIdToHall(any(), any()) }
        }

        @Test
        fun `create should call repo to create, add the trackId if it is not null, retrieve the hall and return it as a Hall`() {
            val trackId = Random.nextInt().absoluteValue
            req = req.copy(trackId = trackId)
            every { hallRepo.addTrackIdToHall(hall.id, trackId) } just Runs

            assertEquals(hall.toHall(), hallAdapter.create(req))

            verify { hallRepo.addTrackIdToHall(hall.id, trackId) }
        }
    }

    @Nested
    inner class UpdateTests {
        private lateinit var hall: HallDB

        @BeforeEach
        fun setUp() {
            hall = HallDBGen().generateOne()

            every { hallRepo.findById(hall.id) } returns Optional.of(hall)
        }

        @AfterEach
        fun breakDown() {
            verify { hallRepo.findById(hall.id) }
        }

        @Test
        fun `associateToEvent should call repo and return the result as a Hall`() {
            val eventId = Random.nextInt().absoluteValue
            every { hallRepo.associateToEvent(hall.id, eventId) } just Runs

            assertEquals(hall.toHall(), hallAdapter.associateToEvent(hall.id, eventId))

            verify { hallRepo.associateToEvent(hall.id, eventId) }
        }

        @Test
        fun `dissociateFromEvent should call repo and return the result as a Hall`() {
            val eventId = Random.nextInt().absoluteValue
            every { hallRepo.dissociateFromEvent(hall.id, eventId) } just Runs

            assertEquals(hall.toHall(), hallAdapter.dissociateFromEvent(hall.id, eventId))

            verify { hallRepo.dissociateFromEvent(hall.id, eventId) }
        }

        @ParameterizedTest
        @ValueSource(booleans = [false, true])
        fun `setOrderInEvent should call repo and return the result as a Hall`(orderNotNull: Boolean) {
            val eventId = Random.nextInt().absoluteValue
            val order = if (orderNotNull) Random.nextInt().absoluteValue else null
            every { hallRepo.setOrderInEvent(hall.id, eventId, order) } just Runs

            assertEquals(hall.toHall(), hallAdapter.setOrderInEvent(hall.id, eventId, order))

            verify { hallRepo.setOrderInEvent(hall.id, eventId, order) }
        }

        @ParameterizedTest
        @EnumSource(UpdateCases::class)
        fun `update should call repo and return the result as a Hall`(case: UpdateCases) {
            val (request, result) = when(case) {
                UpdateCases.NoUpdate -> {
                    Pair(HallPatchReq(requireNotNull(hall.name), hall.trackId), hall)
                }
                UpdateCases.UpdateName -> {
                    val newName = generateRandomHexString()
                    Pair(HallPatchReq(newName, hall.trackId), hall.copy(name = newName))
                }
                UpdateCases.UpdateTrackId -> {
                    val newTrackId = Random.nextInt().absoluteValue
                    Pair(HallPatchReq(requireNotNull(hall.name), newTrackId), hall.copy(trackId = newTrackId))
                }
            }
            every { hallRepo.save(result) } returns result

            assertEquals(result.toHall(), hallAdapter.update(hall.id, request))

            verify { hallRepo.save(result) }
        }
    }

    @Test
    fun `delete should call repo`() {
        val id = Random.nextInt().absoluteValue
        every { hallRepo.deleteById(id) } just Runs

        hallAdapter.delete(id)

        verify { hallRepo.deleteById(id) }
    }
}