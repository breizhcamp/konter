package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(HallAdapter::class)
class HallAdapterTest {

    @MockkBean
    private lateinit var hallRepo: HallRepo

    @Autowired
    private lateinit var hallAdapter: HallAdapter

    @Test
    fun list() {
    }

    @Test
    fun create() {
    }

    @Test
    fun associateToEvent() {
    }

    @Test
    fun dissociateFromEvent() {
    }

    @Test
    fun setOrderInEvent() {
    }

    @Test
    fun update() {
    }

    @Test
    fun delete() {
    }
}