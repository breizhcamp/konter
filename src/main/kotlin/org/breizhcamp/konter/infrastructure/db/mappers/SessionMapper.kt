package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.infrastructure.db.model.SessionDB

fun SessionDB.toSession() = Session(
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
    event = event.toEvent(),
    hall = hall?.toHall(),
    beginning = beginning,
    end = end,
    videoURL = videoURL,
    rating = rating
)

fun Session.toDB() = SessionDB(
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
    hall = hall?.toDB(),
    beginning = beginning,
    end = end,
    videoURL = videoURL,
    rating = rating
)