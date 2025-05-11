package org.breizhcamp.konter.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.konter.domain.entities.Speaker
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SpeakerExport(
    private val speakerPort: SpeakerPort
) {
    fun export(): List<Speaker> {
        logger.info { "Exporting speakers from use case" }
        return speakerPort.listWithSessionAndSlot()
    }
} 