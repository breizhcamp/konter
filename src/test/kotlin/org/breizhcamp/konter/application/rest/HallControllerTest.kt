package org.breizhcamp.konter.application.rest

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.HallAssociateEvent
import org.breizhcamp.konter.domain.use_cases.HallCRUD
import org.breizhcamp.konter.domain.use_cases.HallSetOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(HallController::class)
class HallControllerTest {

    @MockkBean
    private lateinit var hallCRUD: HallCRUD

    @MockkBean
    private lateinit var hallAssociateEvent: HallAssociateEvent

    @MockkBean
    private lateinit var hallSetOrder: HallSetOrder

    @Autowired
    private lateinit var hallController: HallController

    @Test
    fun listAll() {
    }

    @Test
    fun createHall() {
    }

    @Test
    fun listByEvent() {
    }

    @Test
    fun patchHall() {
    }

    @Test
    fun deleteHall() {
    }

    @Test
    fun associateToEvent() {
    }

    @Test
    fun dissociateFromEvent() {
    }

    @Test
    fun updateOrderForEvent() {
    }
}