package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.infrastructure.db.repos.ManualSessionRepo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(ManualSessionAdapter::class)
class ManualSessionAdapterTest {

    @MockkBean
    private lateinit var manualSessionRepo: ManualSessionRepo

    @Autowired
    private lateinit var manualSessionAdapter: ManualSessionAdapter

    @Test
    fun create() {
    }

    @Test
    fun getById() {
    }

    @Test
    fun getAllByEventId() {
    }

    @Test
    fun update() {
    }

    @Test
    fun delete() {
    }
}