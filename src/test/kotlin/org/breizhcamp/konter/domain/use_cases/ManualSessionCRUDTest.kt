package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.application.requests.SessionCreationReq
import org.breizhcamp.konter.application.requests.SessionPatchReq
import org.breizhcamp.konter.domain.entities.ManualSession
import org.breizhcamp.konter.domain.use_cases.ports.ManualSessionPort
import org.breizhcamp.konter.testUtils.ManualSessionGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(ManualSessionCRUD::class)
class ManualSessionCRUDTest {

    @MockkBean
    private lateinit var manualSessionPort: ManualSessionPort

    @Autowired
    private lateinit var manualSessionCRUD: ManualSessionCRUD

    @Nested
    inner class CRUTests {
        private lateinit var session: ManualSession

        @BeforeEach
        fun setUp() {
            session = ManualSessionGen().generateOne()
        }

        @Test
        fun `create should call Port with its inputs and return the result`() {
            val req = SessionCreationReq(
                title = session.title,
                description = session.description,
                format = session.format,
                theme = session.theme
            )
            val eventId = Random.nextInt().absoluteValue

            every { manualSessionPort.create(req, eventId) } returns session

            assertEquals(session, manualSessionCRUD.create(req, eventId))

            verify { manualSessionPort.create(req, eventId) }
        }

        @Test
        fun `get should call Port with its input and return the result`() {
            every { manualSessionPort.getById(session.id) } returns session

            assertEquals(session, manualSessionCRUD.get(session.id))

            verify { manualSessionPort.getById(session.id) }
        }

        @Test
        fun `update should call Port with its inputs and return the result`() {
            val req = SessionPatchReq(
                title = session.title,
                description = session.description,
                format = session.format,
                theme = session.theme
            )
            every { manualSessionPort.update(session.id, req) } returns session

            assertEquals(session, manualSessionCRUD.update(session.id, req))

            verify { manualSessionPort.update(session.id, req) }
        }
    }

    @Test
    fun `list should call Port with its input and return the result`() {
        val sessions = ManualSessionGen().generateList()
        val eventId = Random.nextInt().absoluteValue
        every { manualSessionPort.getAllByEventId(eventId) } returns sessions

        assertEquals(sessions, manualSessionCRUD.list(eventId))

        verify { manualSessionPort.getAllByEventId(eventId) }
    }

    @Test
    fun `delete should call Port with its input`() {
        val id = Random.nextInt().absoluteValue
        every { manualSessionPort.delete(id) } just Runs

        manualSessionCRUD.delete(id)

        verify { manualSessionPort.delete(id) }
    }
}