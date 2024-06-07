package org.breizhcamp.konter.infrastructure.db

import com.ninjasquad.springmockk.MockkBean
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.breizhcamp.konter.infrastructure.db.repos.SpeakerRepo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(SpeakerAdapter::class)
class SpeakerAdapterTest {

    @MockkBean
    private lateinit var speakerRepo: SpeakerRepo

    @Autowired
    private lateinit var speakerAdapter: SpeakerAdapter

    @Test
    fun list() {
    }

    @Test
    fun filter() {
    }

    @Test
    fun get() {
    }

    @Test
    fun getByNameAndEmail() {
    }

    @Test
    fun save() {
    }
}