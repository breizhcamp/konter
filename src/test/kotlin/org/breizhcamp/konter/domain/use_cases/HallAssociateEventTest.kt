package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(HallAssociateEvent::class)
class HallAssociateEventTest {

    @MockkBean
    private lateinit var hallPort: HallPort

    @Autowired
    private lateinit var hallAssociateEvent: HallAssociateEvent

    @Test
    fun associate() {
    }

    @Test
    fun dissociate() {
    }
}