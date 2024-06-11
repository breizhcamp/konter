package org.breizhcamp.konter.infrastructure.kalon

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpHeaders
import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.testUtils.EventGen
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.client.MockServerClient
import org.mockserver.configuration.Configuration
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.HttpStatusCode
import org.mockserver.model.MediaType
import org.mockserver.model.RequestDefinition
import org.slf4j.event.Level
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(KalonAdapter::class)
class KalonAdapterTest {

    @Autowired
    private lateinit var kalonAdapter: KalonAdapter

    private lateinit var server: MockServerClient

    private lateinit var getIdsRequestDefinition: RequestDefinition

    private lateinit var getEventRequestDefinitions: List<RequestDefinition>

    private lateinit var events: List<Event>

    @BeforeEach
    fun setUp() {
        events = EventGen().generateList()
        val mockServerConfig = Configuration()
        mockServerConfig.logLevel(Level.WARN)
        server = ClientAndServer.startClientAndServer(mockServerConfig, 9999)
        setUpGetEventsIds()
        setUpGetEventById()
    }

    @AfterEach
    fun breakDown() {
        server.close()
    }

    private fun setUpGetEventsIds() {
        val requestDefinition = request()
            .withMethod("GET")
            .withPath("/api/events")

        server.`when`(requestDefinition).respond(response()
            .withStatusCode(HttpStatusCode.OK_200.code())
            .withHeaders(
                Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF_8.toString())
            )
            .withBody(ObjectMapper().writeValueAsString(events.map { it.id }))
        )

        getIdsRequestDefinition = requestDefinition
    }

    private fun setUpGetEventById() {
        val requestDefinitions: MutableList<RequestDefinition> = mutableListOf()

        for (event in events) {
            val reqDef = request()
                .withMethod("GET")
                .withPath("/api/events/" + event.id)

            server.`when`(reqDef).respond(response()
                .withStatusCode(200)
                .withHeaders(
                    Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF_8.toString())
                )
                .withBody(ObjectMapper().writeValueAsString(event))
            )

            requestDefinitions.add(reqDef)
        }

        getEventRequestDefinitions = requestDefinitions
    }

    @Test
    fun `getEvents should call log, call the kalon server to get the ids, log the number of ids, call the server for each id and return a List of Events`(
        output: CapturedOutput
    ) {
        assertEquals(events, kalonAdapter.getEvents())
        assert(output.contains("Calling Kalon to get all existing events' ids"))
        assert(output.contains("Calling Kalon to get [${events.size}] events"))

        server.verify(getIdsRequestDefinition)
        for (definition in getEventRequestDefinitions) {
            server.verify(definition)
        }
    }
}