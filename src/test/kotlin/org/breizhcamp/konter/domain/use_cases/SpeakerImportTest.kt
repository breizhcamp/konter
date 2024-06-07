package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SpeakerImport::class)
class SpeakerImportTest {

    @MockkBean
    private lateinit var speakerPort: SpeakerPort

    @Autowired
    private lateinit var speakerImport: SpeakerImport

    @Test
    fun importCsv() {
    }

    @Test
    fun getSpeakerPort() {
    }
}