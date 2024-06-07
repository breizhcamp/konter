package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(HallCRUD::class)
class HallCRUDTest {

    @MockkBean
    private lateinit var hallPort: HallPort

    @Autowired
    private lateinit var hallCRUD: HallCRUD

    @Test
    fun create() {
    }

    @Test
    fun update() {
    }

    @Test
    fun delete() {
    }

    @Test
    fun listAll() {
    }

    @Test
    fun listByEvent() {
    }
}