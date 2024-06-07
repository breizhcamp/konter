package org.breizhcamp.konter.application.rest

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.EventGet
import org.breizhcamp.konter.domain.use_cases.SlotAssociateHall
import org.breizhcamp.konter.domain.use_cases.SlotCRUD
import org.breizhcamp.konter.domain.use_cases.SlotGenerateProgram
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SlotController::class)
class SlotControllerTest {

    @MockkBean
    private lateinit var slotCrud: SlotCRUD

    @MockkBean
    private lateinit var eventGet: EventGet

    @MockkBean
    private lateinit var slotGenerateProgram: SlotGenerateProgram

    @MockkBean
    private lateinit var slotAssociateHall: SlotAssociateHall

    @Autowired
    private lateinit var slotController: SlotController

    @Test
    fun listSlotForEvent() {
    }

    @Test
    fun addSlotToHall() {
    }

    @Test
    fun getSlot() {
    }

    @Test
    fun deleteSlot() {
    }

    @Test
    fun exportProgram() {
    }

    @Test
    fun assignHallToSlot() {
    }

    @Test
    fun resignHallFromSlot() {
    }
}