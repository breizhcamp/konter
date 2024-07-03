package org.breizhcamp.konter.domain.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class Event(
    val id: Int,
    val year: Int,
    val name: String?,
    @JsonProperty("debutEvent")
    val begin: LocalDate?,
    @JsonProperty("finEvent")
    val end: LocalDate?,
)
