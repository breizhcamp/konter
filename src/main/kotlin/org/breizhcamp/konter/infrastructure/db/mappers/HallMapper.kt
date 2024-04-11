package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.infrastructure.db.model.HallDB

fun HallDB.toHall() = Hall(
    id = id,
    name = name
)

fun Hall.toDB() = HallDB(
    id = id,
    name = name
)