package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SessionGenerateCards::class)
class SessionGenerateCardsTest {

    @MockkBean
    private lateinit var sessionPort: SessionPort

    @Autowired
    private lateinit var sessionGenerateCards: SessionGenerateCards

    @Test
    @Disabled("Discuss PDF generation testing")
    fun generatePdf(){
    }
}