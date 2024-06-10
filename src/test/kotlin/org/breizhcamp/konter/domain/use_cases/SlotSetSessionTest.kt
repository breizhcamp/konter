package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.breizhcamp.konter.testUtils.SessionGen
import org.breizhcamp.konter.testUtils.generateRandomHexString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(SlotSetSession::class)
class SlotSetSessionTest {

    @MockkBean
    private lateinit var sessionPort: SessionPort

    @Autowired
    private lateinit var slotSetSession: SlotSetSession

    private lateinit var session: Session

    @BeforeEach
    fun setUp() {
        session = SessionGen().generateOne()
    }

    @Test
    fun `setById should call Port with its inputs and return the result`() {
        val slotId = UUID.randomUUID()
        every { sessionPort.setSlotById(session.id, slotId) } returns session

        assertEquals(session, slotSetSession.setById(session.id, slotId))

        verify { sessionPort.setSlotById(session.id, slotId) }
    }

    @Test
    fun `setByBarcode should call Port with its inputs and return the result`() {
        val barcode = generateRandomHexString()
        every { sessionPort.setSlotByBarcode(session.id, barcode) } returns session

        assertEquals(session, slotSetSession.setByBarcode(session.id, barcode))

        verify { sessionPort.setSlotByBarcode(session.id, barcode) }
    }
}