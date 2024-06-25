package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Slot
import org.breizhcamp.konter.infrastructure.db.model.SlotDB

fun SlotDB.toSlot(): Slot = Slot(
    id = id,
    day = day,
    start = start,
    duration = duration,
    halls = halls.map { it.toHall() },
    session = session?.toLimitedSession(),
    manualSession = manualSession?.toManualSession(),
    event = event.toEvent(),
    barcode = barcode,
    span = 1,
    title = title,
    assignable = assignable
)

fun SlotDB.toLimitedSlot(): Slot = Slot(
    id = id,
    day = day,
    start = start,
    duration = duration,
    halls = halls.map { it.toHall() },
    session = null,
    manualSession = null,
    event = event.toEvent(),
    barcode = barcode,
    span = 1,
    title = title,
    assignable = assignable
)

fun Slot.toDB(): SlotDB = SlotDB(
    id = id,
    day = day,
    session = session?.toDB(),
    manualSession = manualSession?.toDB(),
    event = event.toDB(),
    halls = halls.map{ it.toDB() }.toSet(),
    start = start,
    duration = duration,
    barcode = barcode,
    title = title,
    assignable = assignable
)