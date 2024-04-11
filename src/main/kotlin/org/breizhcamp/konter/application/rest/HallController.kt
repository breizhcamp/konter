package org.breizhcamp.konter.application.rest

import org.breizhcamp.konter.application.dto.HallDTO
import org.breizhcamp.konter.domain.entities.Hall
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/halls")
class HallController {
}

fun Hall.toDto() = HallDTO(
    id = id,
    name = name,
)