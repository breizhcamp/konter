package org.breizhcamp.konter.infrastructure.db

import com.itextpdf.barcodes.BarcodeEAN
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.breizhcamp.konter.application.requests.SlotCreationReq
import org.breizhcamp.konter.domain.entities.exceptions.HallNotFoundException
import org.breizhcamp.konter.domain.entities.exceptions.TimeConflictException
import org.breizhcamp.konter.infrastructure.db.mappers.toHall
import org.breizhcamp.konter.infrastructure.db.mappers.toSlot
import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.breizhcamp.konter.infrastructure.db.model.SlotDB
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.breizhcamp.konter.infrastructure.db.repos.SlotRepo
import org.breizhcamp.konter.testUtils.HallDBGen
import org.breizhcamp.konter.testUtils.SlotDBGen
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.time.LocalTime
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(SlotAdapter::class)
class SlotAdapterTest {

    @MockkBean
    private lateinit var hallRepo: HallRepo

    @MockkBean
    private lateinit var slotRepo: SlotRepo

    @Autowired
    private lateinit var slotAdapter: SlotAdapter

    enum class CreationCases {
        NoOverlapAfter,
        NoOverlapBefore,
        OverlapBefore,
        OverlapAfter,
        OverlapIn,
        OverlapOut
    }

    @ParameterizedTest
    @EnumSource(CreationCases::class)
    fun `throwIfUnavailable should throw if the new slot overlaps in any way`(case: CreationCases) {
        val eventId = Random.nextInt(1, 10)
        val hall = HallDBGen().generateOne().copy(trackId = Random.nextInt(1, 10))

        val existingSlot = SlotDBGen().generateOne().copy(day = Random.nextInt(1, 10))
        val existingSlots = listOf(existingSlot)

        val slotCreationReq = case.toSlotCreationReq(existingSlot, listOf(hall.id))

        every { slotRepo.getByHallIdAndEventId(hall.id, eventId) } returns existingSlots
        every { hallRepo.findById(hall.id) } returns Optional.of(hall)

        when(case) {
            CreationCases.NoOverlapBefore,
            CreationCases.NoOverlapAfter -> {
                assertDoesNotThrow {
                    slotAdapter.throwIfOverlapped(hall.id, eventId, slotCreationReq)
                }
                verify(exactly = 0) { hallRepo.findById(hall.id) }
            }
            CreationCases.OverlapBefore,
            CreationCases.OverlapAfter,
            CreationCases.OverlapIn,
            CreationCases.OverlapOut -> {
                assertThrows<TimeConflictException> {
                    slotAdapter.throwIfOverlapped(hall.id, eventId, slotCreationReq)
                }
                verify { hallRepo.findById(hall.id) }
            }
        }

        verify { slotRepo.getByHallIdAndEventId(hall.id, eventId) }
    }

    enum class HallCases {
        NotFound,
        FoundNoTrackId,
        FoundAndTrackId
    }

    @ParameterizedTest
    @EnumSource(HallCases::class)
    fun `create should call slotRepo, hallRepo, create a slot if hall is found and has a trackId and return the result, or throw an Exception otherwise`(
        case: HallCases
    ) {
        val eventId = Random.nextInt(1, 10)
        var hall = HallDBGen().generateOne().copy(trackId = Random.nextInt(1, 10))

        val existingSlot = SlotDBGen().generateOne().copy(day = Random.nextInt(1, 10))
        val existingSlots = listOf(existingSlot)

        val slotCreationReq = CreationCases.NoOverlapBefore.toSlotCreationReq(existingSlot, listOf(hall.id))

        every { slotRepo.getByHallIdAndEventId(hall.id, eventId) } returns existingSlots

        every { hallRepo.getAllByAvailableEventId(eventId) } returns when(case) {
            HallCases.NotFound -> { emptyList() }
            HallCases.FoundNoTrackId -> {
                hall = hall.copy(trackId = null)
                listOf(hall)
            }
            HallCases.FoundAndTrackId -> {
                listOf(hall)
            }
        }

        val barcode = getBarcode(slotCreationReq, hall.trackId ?: 0, eventId)
        every { slotRepo.create(hall.id, eventId, slotCreationReq.day, slotCreationReq.start, slotCreationReq.duration.seconds, barcode) } just Runs
        val returnedSlot = SlotDBGen()
            .generateOne()
            .copy(
                day = slotCreationReq.day,
                start = slotCreationReq.start,
                duration = slotCreationReq.duration,
                barcode = barcode
            )
        every { slotRepo.getByBarcodeAndEventId(barcode, eventId) } returns returnedSlot

        when(case) {
            HallCases.FoundAndTrackId -> {
                assertEquals(returnedSlot.toSlot(), slotAdapter.create(eventId, slotCreationReq))
                verify { slotRepo.create(
                    hall.id,
                    eventId,
                    slotCreationReq.day,
                    slotCreationReq.start,
                    slotCreationReq.duration.seconds,
                    barcode
                ) }
                verify { slotRepo.getByBarcodeAndEventId(barcode, eventId) }
            }
            else -> {
                assertThrows<HallNotFoundException> { slotAdapter.create(eventId, slotCreationReq) }
                verify(exactly = 0) { slotRepo.create(
                    hall.id,
                    eventId,
                    slotCreationReq.day,
                    slotCreationReq.start,
                    slotCreationReq.duration.seconds,
                    barcode
                ) }
                verify(exactly = 0) { slotRepo.getByBarcodeAndEventId(barcode, eventId) }
            }
        }
        verify { slotRepo.getByHallIdAndEventId(hall.id, eventId) }
        verify { hallRepo.getAllByAvailableEventId(eventId) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `getById should call repo and return the result as a Slot if it exists, or throw an Exception otherwise`(
        exists: Boolean
    ) {
        val slotDB = SlotDBGen().generateOne()
        every { slotRepo.findById(slotDB.id) } returns
                if (exists) Optional.of(slotDB)
                else Optional.empty()

        if (exists) {
            assertEquals(slotDB.toSlot(), slotAdapter.getById(slotDB.id))
        } else {
            assertThrows<NoSuchElementException> { slotAdapter.getById(slotDB.id) }
        }

        verify { slotRepo.findById(slotDB.id) }
    }

    @Test
    fun `getProgram should call repos and generate the expected data structure to be used when generating the PDF`() {
        val eventId = Random.nextInt(1, 10)
        val (slots, halls) = slotsAndHallsForProgram()

        every { slotRepo.getAllByEventId(eventId) } returns slots
        every { hallRepo.getAllByAvailableEventId(eventId) } returns halls

        val result = slotAdapter.getProgram(eventId)
        assertEquals(1, result.size)

        val tracks = requireNotNull(result[result.keys.first()])
        { "Precedent assert should have assured that there is at one entry in the map" }
        assertEquals(halls.size, tracks.size)
        assertEquals(halls.map { it.toHall() }, tracks.keys.toList())
        assert(tracks.size == 2)

        val firstTrack = requireNotNull(tracks[tracks.keys.first()])
            { "Should contain two tracks, so one is expected" }
        val secondTrack = requireNotNull(tracks[tracks.keys.last()])
            { "Should contain two tracks, so a second one is expected" }
        assertNotEquals(firstTrack, secondTrack, "The two tracks should be different")

        assertNotNull(firstTrack.find { it.span > 1 }, "The plenum slot is stored only in the first track and has a span of 2")
        assertNull(secondTrack.find { it.span > 1 }, "All slots in the second track have a span of 1")

        verify { slotRepo.getAllByEventId(eventId) }
        verify { hallRepo.getAllByAvailableEventId(eventId) }
    }

    @Test
    fun `remove should call Repo`() {
        val id = UUID.randomUUID()
        every { slotRepo.deleteById(id) } just Runs
        slotAdapter.remove(id)
        verify { slotRepo.deleteById(id) }
    }

    @Nested
    inner class AssociationTests {
        private lateinit var slot: SlotDB
        private var hallId: Int = 0

        @BeforeEach
        fun setUp() {
            slot = SlotDBGen().generateOne()
            hallId = Random.nextInt().absoluteValue
            every { slotRepo.findById(slot.id) } returns Optional.of(slot)
        }

        @Test
        fun `associateHall should call repo and return the slot`() {
            val eventId = Random.nextInt().absoluteValue
            every { slotRepo.associateToHallAndEvent(slot.id, hallId, eventId) } just Runs

            assertEquals(slot.toSlot(), slotAdapter.associateHall(slot.id, eventId, hallId))

            verify { slotRepo.associateToHallAndEvent(slot.id, hallId, eventId) }
        }

        @Test
        fun `dissociateHall should call repo and return the slot`() {
            every { slotRepo.dissociateFromHall(slot.id, hallId) } just Runs

            assertEquals(slot.toSlot(), slotAdapter.dissociateHall(slot.id, hallId))

            verify { slotRepo.dissociateFromHall(slot.id, hallId) }
        }

        @AfterEach
        fun breakDown() {
            verify { slotRepo.findById(slot.id) }
        }
    }

    private fun CreationCases.toSlotCreationReq(slot: SlotDB, hallIds: List<Int>): SlotCreationReq =
        when (this) {
            CreationCases.NoOverlapBefore -> {
                val start = slot.start - Duration.ofMinutes(Random.nextLong(10, 20))
                val duration = Duration.ofMinutes(Random.nextLong(0, 10))

                SlotCreationReq(start, slot.day, duration, hallIds)
            }
            CreationCases.NoOverlapAfter -> {
                val start = slot.start + slot.duration + Duration.ofMinutes(Random.nextLong(10, 20))
                val duration = Duration.ofMinutes(Random.nextLong(10, 20))

                SlotCreationReq(start, slot.day, duration, hallIds)
            }
            CreationCases.OverlapBefore -> {
                val timeBeforeOther = Duration.ofMinutes(Random.nextLong(10, 20))
                val start = slot.start - timeBeforeOther
                val duration = timeBeforeOther + slot.duration.dividedBy(2)

                SlotCreationReq(start, slot.day, duration, hallIds)
            }
            CreationCases.OverlapAfter -> {
                val start = slot.start + slot.duration.dividedBy(2)
                val duration = slot.duration.dividedBy(2) + Duration.ofMinutes(Random.nextLong(10, 20))

                SlotCreationReq(start, slot.day, duration, hallIds)
            }
            CreationCases.OverlapIn -> {
                val start = slot.start + slot.duration.dividedBy(4)
                val duration = slot.duration.dividedBy(2)

                SlotCreationReq(start, slot.day, duration, hallIds)
            }
            CreationCases.OverlapOut -> {
                val timeBeforeOther = Duration.ofMinutes(Random.nextLong(10, 20))
                val start = slot.start - timeBeforeOther
                val duration = slot.duration.plus(timeBeforeOther.multipliedBy(2))

                SlotCreationReq(start, slot.day, duration, hallIds)
            }
        }

    private fun getBarcode(req: SlotCreationReq, trackId: Int, eventId: Int): String {
        var barcode = "${req.day}$eventId$trackId" +
                req.start.hour.toString().padStart(2, '0') +
                req.start.minute.toString().padStart(2, '0')
        barcode += "0".repeat(12 - barcode.length)
        barcode += BarcodeEAN.calculateEANParity(barcode)

        return barcode
    }

    private fun slotsAndHallsForProgram(): Pair<List<SlotDB>, List<HallDB>> {
        val day = Random.nextInt(1, 10)
        val hallGen = HallDBGen()
        val firstHall = hallGen.generateOne()
        val secondHall = hallGen.generateOne()
        val halls = listOf(firstHall, secondHall)
        val slotGen = SlotDBGen()
        val plenumSlotStart = LocalTime.of(9, 30)
        val plenumSlotDuration = Duration.ofHours(1)
        val plenumSlot = slotGen
            .generateOne()
            .copy(
                halls = halls.toSet(),
                day = day,
                start = plenumSlotStart,
                duration = plenumSlotDuration
            )
        val twoHourSlot = slotGen
            .generateOne()
            .copy(
                day = day,
                halls = setOf(firstHall),
                start = plenumSlotStart + plenumSlotDuration,
                duration = Duration.ofHours(2)
            )
        val firstOneHourSlot = slotGen
            .generateOne()
            .copy(
                day = day,
                halls = setOf(secondHall),
                start = plenumSlotStart + plenumSlotDuration,
                duration = Duration.ofHours(1)
            )
        val secondOneHourSlot = slotGen
            .generateOne()
            .copy(
                day = day,
                halls = setOf(secondHall),
                start = plenumSlotStart + plenumSlotDuration + Duration.ofHours(1),
                duration = Duration.ofHours(1)
            )

        val allSlots = listOf(
            plenumSlot,
            twoHourSlot,
            firstOneHourSlot,
            secondOneHourSlot
        )

        return Pair(allSlots, halls)
    }
}