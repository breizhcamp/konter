package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.infrastructure.db.model.EventDB

fun EventDB.toEvent() = Event(
    id = id,
    year = year,
    name = name,
    begin = begin,
    end = end
)

fun Event.toDB() = EventDB(
    id = id,
    year = year,
    name = name,
    halls = emptySet(),
    begin = begin,
    end = end
)