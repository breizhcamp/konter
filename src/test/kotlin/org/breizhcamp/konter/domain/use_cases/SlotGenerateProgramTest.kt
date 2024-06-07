package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SlotGenerateProgram::class)
class SlotGenerateProgramTest {

    @MockkBean
    private lateinit var slotPort: SlotPort

    @MockkBean
    private lateinit var hallPort: HallPort

    @Autowired
    private lateinit var slotGenerateProgram: SlotGenerateProgram

    @Test
    @Disabled("Discuss PDF generation testing")
    fun generateEmptyProgramPdf() {

    }
}