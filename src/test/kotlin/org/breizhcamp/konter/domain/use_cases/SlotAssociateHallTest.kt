package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(SlotAssociateHall::class)
class SlotAssociateHallTest {

    @MockkBean
    private lateinit var slotPort: SlotPort

    @Autowired
    private lateinit var slotAssociateHall: SlotAssociateHall

    @Test
    fun associate() {
    }

    @Test
    fun dissociate() {
    }
}