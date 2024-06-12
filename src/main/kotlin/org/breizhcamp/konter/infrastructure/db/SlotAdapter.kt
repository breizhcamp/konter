package org.breizhcamp.konter.infrastructure.db

import com.itextpdf.barcodes.BarcodeEAN
import jakarta.transaction.Transactional
import org.breizhcamp.konter.application.requests.SlotCreationReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.Slot
import org.breizhcamp.konter.domain.entities.exceptions.HallNotFoundException
import org.breizhcamp.konter.domain.entities.exceptions.TimeConflictException
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.breizhcamp.konter.infrastructure.db.mappers.*
import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.breizhcamp.konter.infrastructure.db.model.SlotDB
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.breizhcamp.konter.infrastructure.db.repos.SlotRepo
import org.springframework.stereotype.Component
import java.time.LocalTime
import java.util.*

@Component
class SlotAdapter (
    private val slotRepo: SlotRepo,
    private val hallRepo: HallRepo,
): SlotPort {

    @Throws
    @Transactional
    override fun create(hallId: Int, eventId: Int, req: SlotCreationReq): Slot {
        throwIfOverlapped(hallId, eventId, req)

        val hall = hallRepo.findById(hallId)

        val barcode: String?
        if (hall.isPresent && hall.get().trackId != null) {
            barcode = computeBarcode(
                req.day,
                eventId,
                requireNotNull(hall.get().trackId) {
                    "Hall:${hall.get().id}.trackId should not be null at this point"
                },
                req.start
            )
        } else {
            throw HallNotFoundException("Hall with id $hallId not found in database or does not have a trackId assigned")
        }

        slotRepo.create(hallId, eventId, req.day, req.start, req.duration.seconds, barcode)
        return slotRepo.getByBarcodeAndEventId(barcode, eventId).toSlot()
    }

    @Throws
    fun throwIfOverlapped(hallId: Int, eventId: Int, req: SlotCreationReq) {
        val start = req.start
        val end = req.start.plus(req.duration)
        val day = req.day

        val existingSlots: MutableList<SlotDB> =
            slotRepo.getByHallIdAndEventId(hallId, eventId)
                .toMutableList()

        val overlappingSlot = existingSlots.filter {
            it.day == day
        }.map { slot ->
            Pair(slot.start, slot.start.plus(slot.duration))
        }.find { range ->
            (start >= range.first && start < range.second) ||
                    (end > range.first && end <= range.second) ||
                    (start < range.first && end > range.second)
        }

        if (overlappingSlot != null) {
            throw TimeConflictException(
                "Slot overlaps with an existing slot, which begins at " +
                        "${overlappingSlot.first} and ends at " +
                        "${overlappingSlot.second}"
            )
        }
    }

    fun computeBarcode(day: Int, eventId: Int, trackId: Int, start: LocalTime): String {
        val barcodeBuilder = StringBuilder()
        barcodeBuilder.append(day)
        barcodeBuilder.append(eventId)
        barcodeBuilder.append(trackId)
        barcodeBuilder.append("${start.hour}".padStart(2, '0'))
        barcodeBuilder.append("${start.minute}".padStart(2, '0'))
        barcodeBuilder.append("0".repeat(12 - barcodeBuilder.length))

        val result = barcodeBuilder.toString()
        return result + BarcodeEAN.calculateEANParity(result)
    }

    override fun getById(id: UUID): Slot = slotRepo.findById(id).get().toSlot()

    override fun getProgram (eventId: Int): Map<Int, Map<Hall, List<Slot>>> {
        val allSlots = slotRepo.getAllByEventId(eventId)
        val availableHalls = hallRepo.getAllByAvailableEventId(eventId)
        val dayMap = mutableMapOf<Int, MutableMap<Hall, MutableList<Slot>>>()

        val days = allSlots.map { it.day }.toSortedSet()

        for (day in days) {
            val daySlots = allSlots
                .toSet()
                .filter { it.day == day }
                .sortedBy { it.start }
                .toMutableList()
            val tracks = mutableMapOf<Hall, MutableList<Slot>>()

            availableHalls.forEach { hall ->
                val slots = daySlots
                    .filter { it.halls.contains(hall) }
                    .toMutableList()
                val trackSlots = mutableListOf<Slot>()
                for (slot in slots) {
                    val spanSlot = slot.toSpanSlot(availableHalls, hall)
                    val newSlot = slot
                        .copy(halls = slot.halls
                            .filter { spanSlot.halls
                                .find { h -> h.id == it.id } != null
                            }.toSet())
                    slots[slots.indexOf(slot)] = newSlot
                    daySlots[daySlots.indexOf(slot)] = newSlot

                    trackSlots.add(spanSlot)
                }

                tracks[hall.toHall()] = trackSlots
            }

            dayMap[day] = tracks
        }

        return dayMap
    }

    @Transactional
    override fun remove(id: UUID) =
        slotRepo.deleteById(id)

    @Transactional
    override fun associateHall(id: UUID, eventId: Int, hallId: Int): Slot {
        slotRepo.associateToHallAndEvent(id, hallId, eventId)

        return getById(id)
    }

    @Transactional
    override fun dissociateHall(id: UUID, hallId: Int): Slot {
        slotRepo.dissocateFromHall(id, hallId)

        return getById(id)
    }

    private fun SlotDB.toSpanSlot(availableHalls: List<HallDB>, hall: HallDB): Slot {
        val newHalls = halls.toMutableList()
        var span = 0
        var index = availableHalls.indexOf(hall)

        while (index < availableHalls.size && halls.contains(availableHalls[index])) {
            newHalls.remove(availableHalls[index])
            span++
            index++
        }

        return Slot(
            id = id,
            day = day,
            session = session?.toLimitedSession(),
            title = title,
            manualSession = manualSession?.toManualSession(),
            event = event.toEvent(),
            halls = newHalls.map { it.toHall() },
            start = start,
            barcode = barcode,
            duration = duration,
            span = span
        )
    }
}