package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.application.requests.SessionCreationReq
import org.breizhcamp.konter.application.requests.SessionPatchReq
import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import org.breizhcamp.konter.infrastructure.db.mappers.toManualSession
import org.breizhcamp.konter.infrastructure.db.model.ManualSessionDB
import org.breizhcamp.konter.infrastructure.db.repos.ManualSessionRepo
import org.breizhcamp.konter.testUtils.ManualSessionDBGen
import org.breizhcamp.konter.testUtils.generateRandomHexString
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(ManualSessionAdapter::class)
class ManualSessionAdapterTest {

    enum class UpdateCases {
        NoUpdate,
        UpdateTitle,
        UpdateDescription,
        UpdateFormat,
        UpdateTheme
    }

    @MockkBean
    private lateinit var manualSessionRepo: ManualSessionRepo

    @Autowired
    private lateinit var manualSessionAdapter: ManualSessionAdapter

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class CreateAndUpdateTests {
        private lateinit var session: ManualSessionDB

        @BeforeEach
        fun setUp() {
            session = ManualSessionDBGen().generateOne()
            every { manualSessionRepo.findById(session.id) } returns Optional.of(session)
        }

        @AfterEach
        fun breakDown() {
            verify { manualSessionRepo.findById(session.id) }
        }

        @Test
        fun `create should call repo to submit the creation request, call it again to retrieve the session and return it as a ManualSession`() {
            val request = SessionCreationReq(
                title = session.title,
                description = session.description,
                format = session.format,
                theme = session.theme
            )
            val eventId = Random.nextInt().absoluteValue

            every { manualSessionRepo.create(
                eventId = eventId,
                title = request.title,
                description = request.description,
                format = request.format,
                theme = request.theme
            ) } returns session.id

            assertEquals(session.toManualSession(), manualSessionAdapter.create(request, eventId))

            verify { manualSessionRepo.create(
                eventId = eventId,
                title = request.title,
                description = request.description,
                format = request.format,
                theme = request.theme
            ) }
        }

        @ParameterizedTest
        @EnumSource(UpdateCases::class)
        fun `update should call repo to retrieve, only change the fields that are non-null in the request, save the new object and return it as a ManualSession`(case: UpdateCases) {
            val (request, result) = updateTestValuesProvider(session, case)
            every { manualSessionRepo.save(result) } returns result

            assertEquals(result.toManualSession(), manualSessionAdapter.update(session.id, request))

            verify { manualSessionRepo.save(result) }
        }

        private fun updateTestValuesProvider(
            initialSession: ManualSessionDB,
            case: UpdateCases
        ): Pair<SessionPatchReq, ManualSessionDB> {
            return when(case) {
                UpdateCases.NoUpdate ->
                    Pair(SessionPatchReq.empty(), initialSession.copy())
                UpdateCases.UpdateTitle -> {
                    val newTitle = generateRandomHexString(2)
                    Pair(
                        SessionPatchReq.empty().copy(title = newTitle),
                        session.copy(title = newTitle))
                }
                UpdateCases.UpdateDescription -> {
                    val newDescription = generateRandomHexString(6)
                    Pair(
                        SessionPatchReq.empty().copy(description = newDescription),
                        session.copy(description = newDescription))
                }
                UpdateCases.UpdateFormat -> {
                    val newFormat = SessionFormatEnum
                        .entries
                        .filterNot { it == session.format }
                        .random()
                    Pair(
                        SessionPatchReq.empty().copy(format = newFormat),
                        session.copy(format = newFormat))
                }
                UpdateCases.UpdateTheme -> {
                    val newTheme = SessionThemeEnum
                        .entries
                        .filterNot { it == session.theme }
                        .random()
                    Pair(
                        SessionPatchReq.empty().copy(theme = newTheme),
                        session.copy(theme = newTheme))
                }
            }
        }



    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `getById should call repo and return the ManualSession if found, and throw otherwise`(exists: Boolean) {
        val session = ManualSessionDBGen().generateOne()
        every { manualSessionRepo.findById(session.id) } returns
                if (exists) Optional.of(session)
                else Optional.empty()

        if (exists) {
            assertEquals(session.toManualSession(), manualSessionAdapter.getById(session.id))
        } else {
            assertThrows<NoSuchElementException> { manualSessionAdapter.getById(session.id) }
        }

        verify { manualSessionRepo.findById(session.id) }
    }

    @Test
    fun `getAllByEventId should call repo and return the result as a List of ManualSession`() {
        val sessions = ManualSessionDBGen().generateList()
        val eventId = Random.nextInt().absoluteValue
        every { manualSessionRepo.getAllByEventId(eventId) } returns sessions

        assertEquals(sessions.map { it.toManualSession() }, manualSessionAdapter.getAllByEventId(eventId))

        verify { manualSessionRepo.getAllByEventId(eventId) }
    }

    @Test
    fun `delete should call repo`() {
        val id = Random.nextInt().absoluteValue
        every { manualSessionRepo.deleteById(id) } just Runs

        manualSessionAdapter.delete(id)

        verify { manualSessionRepo.deleteById(id) }
    }
}