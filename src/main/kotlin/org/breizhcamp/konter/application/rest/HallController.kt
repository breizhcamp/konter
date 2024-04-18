package org.breizhcamp.konter.application.rest

import mu.KotlinLogging
import org.breizhcamp.konter.application.dto.HallDTO
import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.HallCreate
import org.breizhcamp.konter.domain.use_cases.HallList
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {  }

@RestController
@RequestMapping("/api/halls")
class HallController (
    private val hallCreate: HallCreate,
    private val hallList: HallList,
) {

    @GetMapping
    fun listAll(): List<HallDTO> {
        logger.info { "Listing all Halls" }

        return hallList.listAll().map { it.toDto() }
    }

    @PostMapping
    fun createHall(@RequestBody req: HallCreationReq): HallDTO {
        logger.info { "Creating a Hall with name ${req.name}" }

        return hallCreate.createHall(req).toDto()
    }

    @GetMapping("/{eventId}")
    fun listByEvent(@PathVariable eventId: Int): List<HallDTO> {
        logger.info { "Listing Halls available for Event<$eventId>" }

        return hallList.listByEvent(eventId).map { it.toDto() }
    }
}

fun Hall.toDto() = HallDTO(
    id = id,
    name = name,
)