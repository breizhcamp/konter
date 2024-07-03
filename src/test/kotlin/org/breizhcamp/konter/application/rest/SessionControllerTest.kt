package org.breizhcamp.konter.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.application.requests.SessionCreationReq
import org.breizhcamp.konter.application.requests.SessionPatchReq
import org.breizhcamp.konter.domain.entities.ManualSession
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.domain.use_cases.*
import org.breizhcamp.konter.testUtils.EventGen
import org.breizhcamp.konter.testUtils.ManualSessionGen
import org.breizhcamp.konter.testUtils.SessionGen
import org.breizhcamp.konter.testUtils.generateRandomHexString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.multipart.MultipartFile
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

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

    @Nested
    inner class ListTests {
        private var eventId: Int = 0
        private lateinit var sessions: List<Session>

        @BeforeEach
        fun setUp() {
            eventId = Random.nextInt().absoluteValue
            sessions = SessionGen().generateList()
        }

        @Test
        fun `listSessions should log, call List with its input and return the result as a List of DTOs`(output: CapturedOutput) {
            every { sessionList.list(eventId) } returns sessions

            assertEquals(sessions.map(Session::toDto), sessionController.listSessions(eventId))
            assert(output.contains("Listing Sessions from Event:$eventId"))

            verify { sessionList.list(eventId) }
        }

        @Test
        fun `filterSessions should log, call List with its inputs and return the result as a List of DTOs`(output: CapturedOutput) {
            val filter = SessionFilter.empty()
            every { sessionList.filter(eventId, filter) } returns sessions

            assertEquals(sessions.map(Session::toDto), sessionController.filterSessions(eventId, filter))
            assert(output.contains("Filtering Sessions from Event:$eventId"))

            verify { sessionList.filter(eventId, filter) }
        }
    }

    @Nested
    inner class ImportTests {
        private lateinit var file: MultipartFile

        @BeforeEach
        fun setUp() {
            file = MockMultipartFile(
                "values.csv",
                "values.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "VALUES".toByteArray()
            )
        }

        @Test
        fun `importCsv should log and call Import with the input stream of its received file`(output: CapturedOutput) {
            val eventId = Random.nextInt().absoluteValue
            every { sessionImport.importCsv(eventId, any()) } just Runs

            sessionController.importCsv(eventId, file)
            assert(output.contains("Importing Sessions for Event:$eventId"))

            verify { sessionImport.importCsv(eventId, any()) }
        }

        @Test
        fun `importEvaluationsCsv should log and call Import with the input stream of its received file`(output: CapturedOutput) {
            every { sessionImport.importEvaluationCsv(any()) } just Runs

            sessionController.importEvaluationsCsv(file)
            assert(output.contains("Importing Evaluations"))

            verify { sessionImport.importEvaluationCsv(any()) }
        }
    }

    @Test
    fun `exportCards should log, set the correct headers to the response and call GenerateCards with the response outputStream`(output: CapturedOutput) {
        val event = EventGen().generateOne()
        val contentDisposition = "attachment; filename=\"session_cards_${event.name}.pdf\""
        val response  = MockHttpServletResponse()
        every { eventGet.getById(event.id) } returns event
        every { sessionGenerateCards.generatePdf(event.id, response.outputStream) } just Runs

        sessionController.exportCards(event.id, response)
        assertEquals(contentDisposition, response.getHeader(HttpHeaders.CONTENT_DISPOSITION))
        assertEquals(MediaType.APPLICATION_PDF_VALUE, response.getHeader(HttpHeaders.CONTENT_TYPE))
        assert(output.contains("Generating Sessions cards for Event:${event.id}"))

        verify { eventGet.getById(event.id) }
        verify { sessionGenerateCards.generatePdf(event.id, response.outputStream) }
    }

    @Nested
    inner class SetSessionTests {
        private lateinit var session: Session

        @BeforeEach
        fun setUp() {
            session = SessionGen().generateOne()
        }

        @Test
        fun `setSessionById should log, call slotSetSession with its inputs and return the result as a DTO`(output: CapturedOutput) {
            val slotId = UUID.randomUUID()
            every { slotSetSession.setById(session.id, slotId) } returns session

            assertEquals(session.toDto(), sessionController.setSlot(session.id, slotId))
            assert(output.contains("Setting Session:${session.id} to Slot:$slotId"))

            verify { slotSetSession.setById(session.id, slotId) }
        }

        @Test
        fun `setSessionByBarcode should log, call slotSetSession with its inputs and return the result as a DTO`(output: CapturedOutput) {
            val barcode = generateRandomHexString(3).substring(0..<13)
            every { slotSetSession.setByBarcode(session.id, barcode) } returns session

            assertEquals(session.toDto(), sessionController.setSlot(session.id, barcode))
            assert(output.contains("Setting Session:${session.id} to Slot with barcode $barcode"))

            verify { slotSetSession.setByBarcode(session.id, barcode) }
        }
    }

    @Nested
    inner class ManualSessionEntityCruTests {
        private lateinit var session: ManualSession

        @BeforeEach
        fun setUp() {
            session = ManualSessionGen().generateOne()
        }

        @Test
        fun `createManualSession should log, call CRUD with its inputs and return the result as a DTO`(output: CapturedOutput) {
            val request = SessionCreationReq(
                title = session.title,
                description = session.description,
                format = session.format,
                theme = session.theme
            )
            val eventId = Random.nextInt().absoluteValue

            every { manualSessionCRUD.create(request, eventId) } returns session

            assertEquals(session.toDto(), sessionController.createManualSession(eventId, request))
            assert(output.contains("Creating ManualSession for Event:$eventId with title:${request.title}"))

            verify { manualSessionCRUD.create(request, eventId) }
        }

        @Test
        fun `getManualSession should log, call CRUD and return the result as a DTO`(output: CapturedOutput) {
            every { manualSessionCRUD.get(session.id) } returns session

            assertEquals(session.toDto(), sessionController.getManualSession(session.id))
            assert(output.contains("Retrieving ManualSession:${session.id}"))

            verify { manualSessionCRUD.get(session.id) }
        }

        @Test
        fun `patchManualSession should log, call CRUD with its inputs and return the result as a DTO`(output: CapturedOutput) {
            val request = SessionPatchReq.empty()
            every { manualSessionCRUD.update(session.id, request) } returns session

            assertEquals(session.toDto(), sessionController.patchManualSession(session.id, request))
            assert(output.contains("Updating ManualSession:${session.id}"))

            verify { manualSessionCRUD.update(session.id, request) }
        }

    }

    @Test
    fun `listManualSessions should log, call CRUD and return the result as a List of DTOs`(output: CapturedOutput) {
        val sessions = ManualSessionGen().generateList()
        val eventId = Random.nextInt().absoluteValue

        every { manualSessionCRUD.list(eventId) } returns sessions

        assertEquals(sessions.map(ManualSession::toDto), sessionController.listManualSessions(eventId))
        assert(output.contains("Retrieving all ManualSession in Event:$eventId"))

        verify { manualSessionCRUD.list(eventId) }
    }

    @Test
    fun `deleteManualSession should log and call CRUD`(output: CapturedOutput) {
        val id = Random.nextInt().absoluteValue
        every { manualSessionCRUD.delete(id) } just Runs

        sessionController.deleteManualSession(id)
        assert(output.contains("Deleting ManualSession:$id"))

        verify { manualSessionCRUD.delete(id) }
    }
}