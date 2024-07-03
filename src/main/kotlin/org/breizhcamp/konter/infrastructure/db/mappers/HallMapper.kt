package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.infrastructure.db.model.HallDB

fun HallDB.toHall(): Hall = Hall(
    id = id,
    name = name,
    trackId = trackId,
)

fun Hall.toDB(): HallDB = HallDB(
    id = id,
    name = name,
    trackId = trackId,
)