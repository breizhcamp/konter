package org.breizhcamp.konter.infrastructure.kalon

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(KalonAdapter::class)
class KalonAdapterTest {

    @Autowired
    private lateinit var kalonAdapter: KalonAdapter

    @Test
    fun getEvents() {
    }
}