package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.breizhcamp.konter.testUtils.SessionGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(SessionList::class)
class SessionListTest {

    @MockkBean
    private lateinit var sessionPort: SessionPort

    @Autowired
    private lateinit var sessionList: SessionList

    private lateinit var sessions: List<Session>

    @BeforeEach
    fun setUp() {
        sessions = SessionGen().generateList()
    }

    @Test
    fun `list should call Port with its input and false, and return the result`() {
        val eventId = Random.nextInt().absoluteValue
        every { sessionPort.getAllByEventId(eventId, false) } returns sessions

        assertEquals(sessions, sessionList.list(eventId))

        verify { sessionPort.getAllByEventId(eventId, false) }
    }

    @Test
    fun `filter should call Port with its inputs and return the result`() {
        val eventId = Random.nextInt().absoluteValue
        val filter = SessionFilter.empty()
        every { sessionPort.filterByEventId(eventId, filter) } returns sessions

        assertEquals(sessions, sessionList.filter(eventId, filter))

        verify { sessionPort.filterByEventId(eventId, filter) }
    }
}