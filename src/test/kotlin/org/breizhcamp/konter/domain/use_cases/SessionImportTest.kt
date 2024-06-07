package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.domain.use_cases.ports.KalonPort
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SessionImport::class)
class SessionImportTest {

    @MockkBean
    private lateinit var sessionPort: SessionPort

    @MockkBean
    private lateinit var speakerPort: SpeakerPort

    @MockkBean
    private lateinit var eventPort: EventPort

    @MockkBean
    private lateinit var kalonPort: KalonPort

    @Autowired
    private lateinit var sessionImport: SessionImport

    @Test
    fun importCsv() {
    }

    @Test
    fun importEvaluationCsv() {
    }
}