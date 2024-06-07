package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.infrastructure.db.repos.SessionRepo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(SessionAdapter::class)
class SessionAdapterTest {

    @MockkBean
    private lateinit var sessionRepo: SessionRepo

    @Autowired
    private lateinit var sessionAdapter: SessionAdapter

    @Test
    fun getById() {
    }

    @Test
    fun import() {
    }

    @Test
    fun getAllByEventId() {
    }

    @Test
    fun saveEvaluation() {
    }

    @Test
    fun filterByEventId() {
    }

    @Test
    fun addBarcode() {
    }

    @Test
    fun setSlotById() {
    }

    @Test
    fun setSlotByBarcode() {
    }
}