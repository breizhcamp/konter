package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.infrastructure.db.repos.EventRepo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventAdapter::class)
class EventAdapterTest {

    @MockkBean
    private lateinit var eventRepo: EventRepo

    @Autowired
    private lateinit var eventAdapter: EventAdapter

    @Test
    fun existsByYear() {
    }

    @Test
    fun getById() {
    }

    @Test
    fun getByYear() {
    }

    @Test
    fun save() {
    }

    @Test
    fun testSave() {
    }

    @Test
    fun create() {
    }
}