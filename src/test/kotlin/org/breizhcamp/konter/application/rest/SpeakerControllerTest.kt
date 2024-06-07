package org.breizhcamp.konter.application.rest

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.SpeakerImport
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SpeakerController::class)
class SpeakerControllerTest {

    @MockkBean
    private lateinit var speakerImport: SpeakerImport

    @Autowired
    private lateinit var speakerController: SpeakerController

    @Test
    fun importCsv() {
    }
}