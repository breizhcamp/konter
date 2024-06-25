package org.breizhcamp.konter.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.domain.use_cases.SpeakerImport
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
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
    fun `importCsv should log and pass the input stream of the provided file to Import`(output: CapturedOutput) {
        val file = MockMultipartFile(
            "values.csv",
            "values.csv",
            MediaType.TEXT_PLAIN_VALUE,
            "VALUES".toByteArray()
        )

        every { speakerImport.importCsv(any()) } just Runs

        speakerController.importCsv(file)
        assert(output.contains("Importing Speakers"))

        verify { speakerImport.importCsv(any()) }
    }
}