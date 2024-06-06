package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.application.requests.SlotCreationReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.Slot
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class SlotCRUD (
    private val slotPort: SlotPort
) {

    fun create(hallId: Int, eventId: Int, req: SlotCreationReq): Slot = slotPort.create(hallId, eventId, req)
    fun get(id: UUID): Slot = slotPort.getById(id)
    fun list(eventId: Int): Map<Int, Map<Hall, List<Slot>>> = slotPort.getProgram(eventId)
    fun delete(id: UUID) = slotPort.remove(id)

}