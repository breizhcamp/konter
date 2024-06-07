package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.infrastructure.db.repos.SlotRepo
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(SlotAdapter::class)
class SlotAdapterTest {

    @MockkBean
    private lateinit var hallRepo: HallRepo

    @MockkBean
    private lateinit var slotRepo: SlotRepo

    @Autowired
    private lateinit var slotAdapter: SlotAdapter

    @Test
    fun create() {
    }

    @Test
    fun getById() {
    }

    @Test
    fun getProgram() {
    }

    @Test
    fun remove() {
    }

    @Test
    fun associateHall() {
    }

    @Test
    fun dissociateHall() {
    }
}