package org.breizhcamp.konter.testUtils

import org.breizhcamp.konter.domain.entities.*
import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionNiveauEnum
import org.breizhcamp.konter.domain.entities.enums.SessionStatusEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.time.Month
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

fun generateRandomHexString(blocks: Int = 1): String {
    val builder = StringBuilder()

    for (i in 1..blocks) builder.append(
        Random.nextInt().toString(16)
    )

    return builder.toString()
}

fun generateRandomLocalDateTime(year: Int = 2024): LocalDateTime =
    LocalDateTime.of(
        year,
        Month.of(Random.nextInt(1, 12)),
        Random.nextInt(1, 28),
        Random.nextInt(0, 23),
        Random.nextInt(0, 59),
        Random.nextInt(0, 59)
    )

interface Generator<T> {
    fun generateOne(): T

    fun generateList(mSize: Int = 5): List<T> {
        val maxSize = if (mSize < 5) 5 else mSize
        return (1..maxSize).map { generateOne() }
    }
}

class HallGen: Generator<Hall> {
    override fun generateOne(): Hall = Hall(
        id = Random.nextInt().absoluteValue,
        name = generateRandomHexString(),
        trackId = Random.nextInt().absoluteValue
    )
}

class SpeakerGen: Generator<Speaker> {
    override fun generateOne(): Speaker = Speaker(
        id = UUID.randomUUID(),
        lastname = generateRandomHexString(),
        firstname = generateRandomHexString(),
        email = generateRandomHexString(),
        tagLine = generateRandomHexString(),
        bio = generateRandomHexString(),
        profilePicture = generateRandomHexString()
    )
}

class EventGen: Generator<Event> {
    override fun generateOne(): Event = Event(
        id = Random.nextInt().absoluteValue,
        year = Random.nextInt(2020, 2030),
        name = generateRandomHexString()
    )

}

class SessionGen: Generator<Session> {
    override fun generateOne(): Session {
        val speakerGen = SpeakerGen()

        val owner = speakerGen.generateOne()
        val speakers = speakerGen.generateList().toMutableList()
        speakers.addFirst(owner)

        val format = SessionFormatEnum.entries.random()
        val theme  = SessionThemeEnum .entries.random()
        val niveau = SessionNiveauEnum.entries.random()
        val status = SessionStatusEnum.entries.random()

        return Session(
            id = Random.nextInt().absoluteValue,
            title = generateRandomHexString(),
            description = generateRandomHexString(8),
            owner = owner,
            speakers = speakers,
            format = format,
            theme = theme,
            niveau = niveau,
            status = status,
            submitted = generateRandomLocalDateTime(),
            ownerNotes = generateRandomHexString(),
            event = EventGen().generateOne(),
            videoURL = generateRandomHexString(),
            rating = BigDecimal.valueOf(Random.nextDouble(0.0, 5.0)),
            slot = null
        )
    }
}

class ManualSessionGen: Generator<ManualSession> {
    override fun generateOne(): ManualSession {
        val format = SessionFormatEnum.entries.random()
        val theme  = SessionThemeEnum .entries.random()

        return ManualSession(
            id = Random.nextInt().absoluteValue,
            title = generateRandomHexString(),
            description = generateRandomHexString(),
            event = EventGen().generateOne(),
            format = format,
            theme = theme
        )
    }

}

class SlotGen: Generator<Slot> {
    override fun generateOne(): Slot = Slot(
        id = UUID.randomUUID(),
        day = Random.nextInt(0, 5),
        session = SessionGen().generateOne(),
        manualSession = null,
        event = EventGen().generateOne(),
        halls = HallGen().generateList(),
        start = generateRandomLocalDateTime().toLocalTime(),
        duration = Duration.ofMinutes(Random.nextLong(15, 120)),
        barcode = generateRandomHexString(),
        span = Random.nextInt(1, 5),
        title = generateRandomHexString()
    )
}
