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
@WebMvcTest(HallSetOrder::class)
class HallSetOrderTest {

    @MockkBean
    private lateinit var hallPort: HallPort

    @Autowired
    private lateinit var hallSetOrder: HallSetOrder

    private lateinit var hall: Hall

    @BeforeEach
    fun setUp() {
        hall = HallGen().generateOne()
    }

    @Test
    fun `setOrder should call Port with its inputs and return the result`() {
        val eventId = Random.nextInt().absoluteValue
        val order = Random.nextInt().absoluteValue

        every { hallPort.setOrderInEvent(hall.id, eventId, order) } returns hall

        assertEquals(hall, hallSetOrder.setOrder(hall.id, eventId, order))

        verify { hallPort.setOrderInEvent(hall.id, eventId, order) }
    }
}