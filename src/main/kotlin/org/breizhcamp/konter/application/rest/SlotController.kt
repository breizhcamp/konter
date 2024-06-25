package org.breizhcamp.konter.application.rest

import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.breizhcamp.konter.application.dto.SlotDTO
import org.breizhcamp.konter.application.dto.TalkDTO
import org.breizhcamp.konter.application.requests.SlotCreationReq
import org.breizhcamp.konter.application.requests.SlotPatchReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.Slot
import org.breizhcamp.konter.domain.entities.Talk
import org.breizhcamp.konter.domain.entities.enums.getLabel
import org.breizhcamp.konter.domain.entities.exceptions.EventNoBeginException
import org.breizhcamp.konter.domain.entities.exceptions.HallNotFoundException
import org.breizhcamp.konter.domain.entities.exceptions.TimeConflictException
import org.breizhcamp.konter.domain.use_cases.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

private val logger = KotlinLogging.logger {  }

@RestController
@RequestMapping("/api/slots")
class SlotController (
    private val slotCrud: SlotCRUD,
    private val eventGet: EventGet,
    private val slotGenerateProgram: SlotGenerateProgram,
    private val slotAssociateHall: SlotAssociateHall,
    private val getTalks: GetTalks
) {

    @GetMapping("/event/{id}")
    fun listSlotForEvent(@PathVariable id: Int): Map<Int, Map<Int, List<SlotDTO>>> {
        logger.info { "Listing all slots in Event:$id grouping by Hall" }

        val values: Map<Int, Map<Hall, List<Slot>>> = slotCrud.list(id)
        val result = mutableMapOf<Int, MutableMap<Int, List<SlotDTO>>>()

        for (entry in values) {
            val key = entry.key
            val map: Map<Hall, List<Slot>> = entry.value

            val rowMap = mutableMapOf<Int, List<SlotDTO>>()

            for (subEntry in map) {
                val subKey = subEntry.key.id
                val list = subEntry.value.map { it.toDto() }

                rowMap[subKey] = list
            }

            result[key] = rowMap
        }

        return result
    }

    @PostMapping("/{eventId}")
    fun addSlotToHall(
        @PathVariable eventId: Int,
        @RequestBody slotReq: SlotCreationReq
    ): ResponseEntity<Any> {
        logger.info { "Adding slot in Event:$eventId" }

        return try {
            ResponseEntity.ok(slotCrud.create(eventId, slotReq).toDto())
        } catch (e: TimeConflictException) {
            logger.error { e }
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: HallNotFoundException) {
            logger.error { e }
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @GetMapping("/{id}")
    fun getSlot(@PathVariable id: UUID): SlotDTO {
        logger.info { "Retrieving Slot:$id" }

        return slotCrud.get(id).toDto()
    }

    @DeleteMapping("/{id}")
    fun deleteSlot(@PathVariable id: UUID) {
        logger.info { "Deleting Slot:$id" }

        slotCrud.delete(id)
    }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody request: SlotPatchReq): SlotDTO {
        logger.info { "Patching Slot:$id" }

        return slotCrud.update(id, request).toDto()
    }

    @GetMapping("/program/{eventId}")
    fun exportProgram(@PathVariable eventId: Int, output: HttpServletResponse) {
        logger.info { "Generating program for Event:$eventId" }

        val name = StringBuilder()
            .append("attachment; filename=")
            .append("\"program_")
            .append(eventGet.getById(eventId).name)
            .append(".pdf\"")
            .toString()

        output.setHeader(HttpHeaders.CONTENT_DISPOSITION, name)
        output.contentType = MediaType.APPLICATION_PDF_VALUE

        slotGenerateProgram.generateEmptyProgramPdf(eventId, output.outputStream)
    }

    @PostMapping("/hall/{slotId}/{eventId}/{hallId}")
    fun assignHallToSlot(
        @PathVariable slotId: UUID, @PathVariable eventId: Int, @PathVariable hallId: Int
    ): ResponseEntity<Any> {
        logger.info { "Assigning Hall:$hallId to Slot:$slotId in Event:$eventId" }

        return try {
            ResponseEntity.ok(slotAssociateHall.associate(slotId, eventId, hallId).toDto())
        } catch (e: TimeConflictException) {
            logger.error { e }
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        }
    }

    @DeleteMapping("/hall/{slotId}/{hallId}")
    fun resignHallFromSlot(@PathVariable slotId: UUID, @PathVariable hallId: Int) {
        logger.info { "Resigning Hall:$hallId from Slot:$slotId" }

        slotAssociateHall.dissociate(slotId, hallId)
    }

    @GetMapping("/talks/{eventId}")
    fun listTalks(@PathVariable eventId: Int): ResponseEntity<*> {
        logger.info { "Retrieving Talks from Event:$eventId" }

        return try {
            ResponseEntity.ok(getTalks.list(eventId).map { it.toDto() })
        } catch (e: EventNoBeginException) {
            logger.error { e }
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}

fun Slot.toDto(): SlotDTO = SlotDTO(
    id = id,
    day = day,
    session = session?.toDto(),
    halls = halls.map{ it.toDto() },
    start = start,
    duration = duration,
    barcode = barcode,
    span = span,
    title = title,
    assignable = assignable
)

fun Talk.toDto(): TalkDTO {
    return TalkDTO(
        id = id,
        name = name,
        eventStart = eventStart,
        eventEnd = eventEnd,
        eventType = eventType.getLabel(),
        format = format.getLabel(),
        venue = requireNotNull(hall.name) { "Name not found for Hall:${hall.id}" },
        venueId = requireNotNull(hall.trackId) { "Track ID not found for Hall:${hall.id}" },
        speakers = speakers.joinToString(", ") { "${it.firstname} ${it.lastname}" },
        videoUrl = videoUrl,
        filesUrl = filesUrl,
        slidesUrl = slidesUrl,
        description = description
    )
}