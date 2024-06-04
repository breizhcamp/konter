package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.application.requests.SlotCreationReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.Slot
import java.util.*

interface SlotPort {

    @Throws
    fun create(hallId: Int, eventId: Int, req: SlotCreationReq): Slot
    fun getProgram(eventId: Int): Map<Int, Map<Hall, List<Slot>>>
    fun remove(id: UUID)
    fun associateHall(id: UUID, eventId: Int, hallId: Int): Slot
    fun dissociateHall(id: UUID, hallId: Int): Slot

}