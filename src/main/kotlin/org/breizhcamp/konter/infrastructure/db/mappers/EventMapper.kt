package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.infrastructure.db.model.EventDB

fun EventDB.toEvent() = Event(
    id = id,
    year = year,
    name = name
)

fun Event.toDB() = EventDB(
    id = id,
    year = year,
    name = name
)