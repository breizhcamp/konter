package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.application.requests.HallPatchReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.breizhcamp.konter.testUtils.HallGen
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
@WebMvcTest(HallCRUD::class)
class HallCRUDTest {

    @MockkBean
    private lateinit var hallPort: HallPort

    @Autowired
    private lateinit var hallCRUD: HallCRUD

    @Nested
    inner class CUTests {
        private lateinit var hall: Hall

        @BeforeEach
        fun setUp() {
            hall = HallGen().generateOne()
        }

        @Test
        fun `create should call Port with its input and return the result`() {
            val req = HallCreationReq(requireNotNull(hall.name), hall.trackId)
            every { hallPort.create(req) } returns hall

            assertEquals(hall, hallCRUD.create(req))

            verify { hallPort.create(req) }
        }

        @Test
        fun `update should call Port with its inputs and return the result`() {
            val req = HallPatchReq(requireNotNull(hall.name), hall.trackId)
            every { hallPort.update(hall.id, req) } returns hall

            assertEquals(hall, hallCRUD.update(hall.id, req))

            verify { hallPort.update(hall.id, req) }
        }
    }

    @Test
    fun `delete should call Port with its input`() {
        val id = Random.nextInt().absoluteValue
        every { hallPort.delete(id) } just Runs

        hallCRUD.delete(id)

        verify { hallPort.delete(id) }
    }

    @Nested
    inner class ReadListTests {
        private lateinit var halls: List<Hall>

        @BeforeEach
        fun setUp() {
            halls = HallGen().generateList()
        }

        @Test
        fun `listAll should call Port with a null value and return the result`() {
            every { hallPort.list(null) } returns halls

            assertEquals(halls, hallCRUD.listAll())

            verify { hallPort.list(null) }
        }

        @Test
        fun `listByEvent should call Port with its input and return the result`() {
            val eventId = Random.nextInt().absoluteValue
            every { hallPort.list(eventId) } returns halls

            assertEquals(halls, hallCRUD.listByEvent(eventId))

            verify { hallPort.list(eventId) }
        }
    }
}