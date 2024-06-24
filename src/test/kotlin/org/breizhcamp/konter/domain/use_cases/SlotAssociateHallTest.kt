package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.domain.entities.Slot
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.breizhcamp.konter.testUtils.SlotGen
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
@WebMvcTest(SlotAssociateHall::class)
class SlotAssociateHallTest {

    @MockkBean
    private lateinit var slotPort: SlotPort

    @Autowired
    private lateinit var slotAssociateHall: SlotAssociateHall

    private lateinit var slot: Slot
    private var eventId: Int = 0
    private var hallId: Int = 0

    @BeforeEach
    fun setUp() {
        slot = SlotGen().generateOne()
        eventId = Random.nextInt().absoluteValue
        hallId = Random.nextInt().absoluteValue
    }

    @Test
    fun associate() {
        every { slotPort.associateHall(slot.id, eventId, hallId) } returns slot

        assertEquals(slot, slotAssociateHall.associate(slot.id, eventId, hallId))

        verify { slotPort.associateHall(slot.id, eventId, hallId) }
    }

    @Test
    fun dissociate() {
        every { slotPort.dissociateHall(slot.id, hallId) } just Runs

        slotAssociateHall.dissociate(slot.id, hallId)

        verify { slotPort.dissociateHall(slot.id, hallId) }
    }
}