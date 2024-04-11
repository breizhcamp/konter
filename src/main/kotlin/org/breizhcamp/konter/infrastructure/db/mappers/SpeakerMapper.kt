package org.breizhcamp.konter.infrastructure.db.mappers

import org.breizhcamp.konter.domain.entities.Speaker
import org.breizhcamp.konter.infrastructure.db.model.SpeakerDB

fun SpeakerDB.toSpeaker() = Speaker(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    tagLine = tagLine,
    bio = bio,
    profilePicture = profilePicture,
)

fun Speaker.toDB() = SpeakerDB(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    tagLine = tagLine,
    bio = bio,
    profilePicture = profilePicture
)