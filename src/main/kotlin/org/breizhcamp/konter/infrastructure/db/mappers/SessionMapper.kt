package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.infrastructure.db.model.SessionDB

fun SessionDB.toSession(): Session = Session(
    id = id,
    title = title,
    description = description,
    owner = owner.toSpeaker(),
    speakers = speakers.map { it.toSpeaker() },
    format = format,
    theme = theme,
    niveau = niveau,
    status = status,
    submitted = submitted,
    ownerNotes = ownerNotes,
    beginning = beginning,
    end = end,
    videoURL = videoURL,
    rating = rating,
    event = event.toEvent(),
    slot = slot?.toLimitedSlot()
)

fun SessionDB.toLimitedSession(): Session = Session(
    id = id,
    title = title,
    description = description,
    owner = owner.toSpeaker(),
    speakers = speakers.map { it.toSpeaker() },
    format = format,
    theme = theme,
    niveau = niveau,
    status = status,
    submitted = submitted,
    ownerNotes = ownerNotes,
    beginning = beginning,
    end = end,
    videoURL = videoURL,
    rating = rating,
    event = event.toEvent(),
    slot = null
)

fun Session.toDB(): SessionDB = SessionDB(
    id = id,
    title = title,
    description = description,
    owner = owner.toDB(),
    speakers = speakers.map { it.toDB() }.toSet(),
    format = format,
    theme = theme,
    niveau = niveau,
    status = status,
    submitted = submitted,
    ownerNotes = ownerNotes,
    event = event.toDB(),
    beginning = beginning,
    end = end,
    videoURL = videoURL,
    rating = rating,
    barcode = null,
    slot = slot?.toDB()
)