package org.breizhcamp.konter.application.rest

import mu.KotlinLogging
import org.breizhcamp.konter.application.dto.HallDTO
import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.application.requests.HallPatchReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.exceptions.EventNotFoundException
import org.breizhcamp.konter.domain.use_cases.HallAssociateEvent
import org.breizhcamp.konter.domain.use_cases.HallCRUD
import org.breizhcamp.konter.domain.use_cases.HallSetOrder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {  }

@RestController
@RequestMapping("/api/halls")
class HallController (
    private val hallCRUD: HallCRUD,
    private val hallAssociateEvent: HallAssociateEvent,
    private val hallSetOrder: HallSetOrder
) {

    @GetMapping
    fun listAll(): List<HallDTO> {
        logger.info { "Listing all Halls" }

        return hallCRUD.listAll().map { it.toDto() }
    }

    @GetMapping("/{eventId}")
    fun listByEvent(@PathVariable eventId: Int): List<HallDTO> {
        logger.info { "Listing Halls available for Event:$eventId" }

        return hallCRUD.listByEvent(eventId).map { it.toDto() }
    }

    @PostMapping
    fun createHall(@RequestBody req: HallCreationReq): HallDTO {
        logger.info { "Creating a Hall with name ${req.name} and trackId ${req.trackId}" }

        return hallCRUD.create(req).toDto()
    }

    @PatchMapping("/{id}")
    fun patchHall(@PathVariable id: Int, @RequestBody req: HallPatchReq): HallDTO {
        logger.info { "Updating Hall:$id" }

        return hallCRUD.update(id, req).toDto()
    }

    @DeleteMapping("/{id}")
    fun deleteHall(@PathVariable id: Int) {
        logger.info { "Deleting Hall:$id" }

        return hallCRUD.delete(id)
    }

    @PostMapping("/{id}/event/{eventId}/{order}")
    fun associateToEvent(@PathVariable id: Int, @PathVariable eventId: Int, @PathVariable order: Int): ResponseEntity<*> {
        logger.info { "Associating Hall:$id to Event:$eventId" }

        return try {
            ResponseEntity.ok(hallAssociateEvent.associate(id, eventId, order).toDto())
        } catch (e: EventNotFoundException) {
            logger.error { e }

            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/{id}/event/{eventId}")
    fun dissociateFromEvent(@PathVariable id: Int, @PathVariable eventId: Int): HallDTO {
        logger.info { "Dissociating Hall:$id from Event:$eventId" }

        return hallAssociateEvent.dissociate(id, eventId).toDto()
    }

    @PatchMapping("/{id}/event/{eventId}/{order}")
    fun updateOrderForEvent(@PathVariable id: Int, @PathVariable eventId: Int, @PathVariable order: Int): HallDTO {
        logger.info { "Updating order of Hall:$id for Event:$eventId to $order" }

        return hallSetOrder.setOrder(id, eventId, order).toDto()
    }

}

fun Hall.toDto() = HallDTO(
    id = id,
    name = name,
    trackId = trackId
)