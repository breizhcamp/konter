package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.breizhcamp.konter.testUtils.HallGen
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
@WebMvcTest(HallAssociateEvent::class)
class HallAssociateEventTest {

    @MockkBean
    private lateinit var hallPort: HallPort

    @Autowired
    private lateinit var hallAssociateEvent: HallAssociateEvent

    private lateinit var hall: Hall
    private var eventId: Int = 0

    @BeforeEach
    fun setUp() {
        hall = HallGen().generateOne()
        eventId = Random.nextInt().absoluteValue
    }
    
    @Test
    fun `associate should call Port with its inputs and return the result`() {
        every { hallPort.associateToEvent(hall.id, eventId) } returns hall

        assertEquals(hall, hallAssociateEvent.associate(hall.id, eventId))

        verify { hallPort.associateToEvent(hall.id, eventId) }
    }

    @Test
    fun `dissociate should call Port with its inputs and return the result`() {
        every { hallPort.dissociateFromEvent(hall.id, eventId) } returns hall

        assertEquals(hall, hallAssociateEvent.dissociate(hall.id, eventId))

        verify { hallPort.dissociateFromEvent(hall.id, eventId) }
    }
}