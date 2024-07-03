package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.konter.infrastructure.db.mappers.toSpeaker
import org.breizhcamp.konter.infrastructure.db.model.SpeakerDB
import org.breizhcamp.konter.infrastructure.db.repos.SpeakerRepo
import org.breizhcamp.konter.testUtils.SpeakerDBGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(SpeakerAdapter::class)
class SpeakerAdapterTest {

    @MockkBean
    private lateinit var speakerRepo: SpeakerRepo

    @Autowired
    private lateinit var speakerAdapter: SpeakerAdapter

    @Test
    fun `list should call repo and return the result as a List of Speaker`() {
        val speakers = SpeakerDBGen().generateList()
        every { speakerRepo.findAll() } returns speakers

        assertEquals(speakers.map { it.toSpeaker() }, speakerAdapter.list())

        verify { speakerRepo.findAll() }
    }

    @Nested
    inner class CRTests {
        private lateinit var speaker: SpeakerDB

        @BeforeEach
        fun setUp() {
            speaker = SpeakerDBGen().generateOne()
        }

        @ParameterizedTest
        @ValueSource(booleans = [false, true])
        fun `get should call repo, return the result as a Speaker if it was found and throw if not`(exists: Boolean) {
            every { speakerRepo.findById(speaker.id) } returns
                    if (exists) Optional.of(speaker) else Optional.empty()

            if (exists) {
                assertEquals(speaker.toSpeaker(), speakerAdapter.get(speaker.id))
            } else {
                assertThrows<NoSuchElementException> { speakerAdapter.get(speaker.id) }
            }

            verify { speakerRepo.findById(speaker.id) }
        }

        @Test
        fun `getByNameAndEmail should call repo and return the result as a Speaker`() {
            val name = "${speaker.lastname} ${speaker.firstname}"
            every { speakerRepo.findByNameAndEmail(name, speaker.email) } returns speaker

            assertEquals(speaker.toSpeaker(), speakerAdapter.getByNameAndEmail(name, speaker.email))

            verify { speakerRepo.findByNameAndEmail(name, speaker.email) }
        }

        @Test
        fun `save should call repo`() {
            every { speakerRepo.save(speaker) } returns speaker

            speakerAdapter.save(speaker.toSpeaker())

            verify { speakerRepo.save(speaker) }
        }
    }
}