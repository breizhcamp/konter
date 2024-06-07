package org.breizhcamp.konter.application.rest

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SessionController::class)
class SessionControllerTest {

    @MockkBean
    private lateinit var sessionImport: SessionImport

    @MockkBean
    private lateinit var sessionGenerateCards: SessionGenerateCards

    @MockkBean
    private lateinit var eventGet: EventGet

    @MockkBean
    private lateinit var sessionList: SessionList

    @MockkBean
    private lateinit var slotSetSession: SlotSetSession

    @MockkBean
    private lateinit var manualSessionCRUD: ManualSessionCRUD

    @Autowired
    private lateinit var sessionController: SessionController

    @Test
    fun listSessions() {
    }

    @Test
    fun filterSessions() {
    }

    @Test
    fun importCsv() {
    }

    @Test
    fun importEvaluationsCsv() {
    }

    @Test
    fun exportCards() {
    }

    @Test
    fun setSession() {
    }

    @Test
    fun testSetSession() {
    }

    @Test
    fun createManualSession() {
    }

    @Test
    fun getManualSession() {
    }

    @Test
    fun listManualSessions() {
    }

    @Test
    fun patchManualSession() {
    }

    @Test
    fun deleteManualSession() {
    }
}