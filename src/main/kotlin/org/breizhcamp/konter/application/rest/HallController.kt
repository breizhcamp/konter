package org.breizhcamp.konter.application.rest

import mu.KotlinLogging
import org.breizhcamp.konter.application.dto.HallDTO
import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.application.requests.HallPatchReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.HallAssociateEvent
import org.breizhcamp.konter.domain.use_cases.HallCRUD
import org.breizhcamp.konter.domain.use_cases.HallList
import org.breizhcamp.konter.domain.use_cases.HallSetOrder
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {  }

@RestController
@RequestMapping("/api/halls")
class HallController (
    private val hallList: HallList,
    private val hallCRUD: HallCRUD,
    private val hallAssociateEvent: HallAssociateEvent,
    private val hallSetOrder: HallSetOrder
) {

    @GetMapping
    fun listAll(): List<HallDTO> {
        logger.info { "Listing all Halls" }

        return hallList.listAll().map { it.toDto() }
    }

    @PostMapping
    fun createHall(@RequestBody req: HallCreationReq): HallDTO {
        logger.info { "Creating a Hall with name ${req.name} and trackId ${req.trackId}" }

        return hallCRUD.create(req).toDto()
    }

    @GetMapping("/{eventId}")
    fun listByEvent(@PathVariable eventId: Int): List<HallDTO> {
        logger.info { "Listing Halls available for Event:$eventId" }

        return hallList.listByEvent(eventId).map { it.toDto() }
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

    @PostMapping("/{id}/event/{eventId}")
    fun associateToEvent(@PathVariable id: Int, @PathVariable eventId: Int): HallDTO {
        logger.info { "Associating Hall:$id to Event:$eventId" }

        return hallAssociateEvent.associate(id, eventId).toDto()
    }

    @DeleteMapping("/{id}/event/{eventId}")
    fun dissociateFromEvent(@PathVariable id: Int, @PathVariable eventId: Int): HallDTO {
        logger.info { "Dissociating Hall:$id from Event:$eventId" }

        return hallAssociateEvent.dissociate(id, eventId).toDto()
    }

    @PatchMapping("/{id}/event/{eventId}/{order}")
    fun updateOrderForEvent(@PathVariable id: Int, @PathVariable eventId: Int, @PathVariable order: Int?) {
        logger.info { "Updating order of Hall:$id for Event:$eventId to $order" }

        hallSetOrder.setOrder(id, eventId, order)
    }

}

fun Hall.toDto() = HallDTO(
    id = id,
    name = name,
    trackId = trackId
)