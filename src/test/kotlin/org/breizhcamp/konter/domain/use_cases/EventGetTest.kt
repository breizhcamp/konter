package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.testUtils.EventGen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventGet::class)
class EventGetTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventGet: EventGet

    @Test
    fun `getById should call port with its input and return the result`() {
        val event = EventGen().generateOne()
        every { eventPort.getById(event.id) } returns event

        assertEquals(event, eventGet.getById(event.id))

        verify { eventPort.getById(event.id) }
    }
}