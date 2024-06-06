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
import org.breizhcamp.konter.infrastructure.db.repos.EventRepo
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class SlotAdapter (
    private val hallRepo: HallRepo,
    private val eventRepo: EventRepo
): SlotPort {

    @Throws
    @Transactional
    override fun create(hallId: Int, eventId: Int, req: SlotCreationReq): Slot {
        val start = req.start
        val end = req.start.plus(req.duration)
        val day = req.day

        val existingSlots: MutableList<SlotDB> = hallRepo.getSlotsByHallIdAndEventId(hallId, eventId).toMutableList()

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

        val hall = hallRepo.findById(hallId)

        val barcode: String?
        if (hall.isPresent && hall.get().trackId != null) {
            val barcodeBuilder = StringBuilder()
            barcodeBuilder.append(day)
            barcodeBuilder.append(eventId)
            barcodeBuilder.append(hallId)
            barcodeBuilder.append("${start.hour}".padStart(2, '0'))
            barcodeBuilder.append("${start.minute}".padStart(2, '0'))
            barcodeBuilder.append("0".repeat(12 - barcodeBuilder.length))
            barcodeBuilder.append(BarcodeEAN.calculateEANParity(barcodeBuilder.toString()))

            barcode = barcodeBuilder.toString()
        } else {
            throw HallNotFoundException("Hall with id $hallId not found in database")
        }

        hallRepo.addSlotToHall(hallId, eventId, req.day, req.start, req.duration.seconds, barcode)
        return hallRepo.getSlotByBarcode(barcode).toSlot()
    }

    override fun getById(id: UUID): Slot = hallRepo.getSlotById(id).toSlot()

    override fun getProgram (eventId: Int): Map<Int, Map<Hall, List<Slot>>> {
        val allSlots = eventRepo.getAllSlotsByEventId(eventId)
        val availableHalls = hallRepo.getAllByAvailableEventId(eventId)
        val dayMap = emptyMap<Int, MutableMap<Hall, MutableList<Slot>>>().toMutableMap()

        val days = allSlots.map { it.day }.toSortedSet()

        for (day in days) {
            val daySlots = allSlots
                .toSet()
                .filter { it.day == day }
                .sortedBy { it.start }
                .toMutableList()
            val tracks = emptyMap<Hall, MutableList<Slot>>().toMutableMap()

            availableHalls.forEach { hall ->
                val slots = daySlots
                    .filter { it.halls.contains(hall) }
                    .toMutableList()
                val trackSlots = emptyList<Slot>().toMutableList()
                for (slot in slots) {
                    val spanSlot = slot.toSpanSlot(availableHalls, hall)
                    slots[slots.indexOf(slot)] = slot
                        .copy(halls = slot.halls
                            .filter { spanSlot.halls
                                .find { h -> h.id == it.id } != null
                            }.toSet())
                    daySlots[daySlots.indexOf(slot)] = slot
                        .copy(halls = slot.halls
                            .filter { spanSlot.halls
                                .find { h -> h.id == it.id } != null
                            }.toSet())

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
        hallRepo.removeSlot(id)

    @Transactional
    override fun associateHall(id: UUID, eventId: Int, hallId: Int): Slot {
        hallRepo.associateSlot(hallId, eventId, id)

        return hallRepo.getSlotById(id).toSlot()
    }

    @Transactional
    override fun dissociateHall(id: UUID, hallId: Int): Slot {
        hallRepo.dissociateSlot(hallId, id)

        return hallRepo.getSlotById(id).toSlot()
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