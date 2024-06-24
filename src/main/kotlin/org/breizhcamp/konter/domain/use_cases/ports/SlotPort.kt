package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.application.requests.SlotCreationReq
import org.breizhcamp.konter.application.requests.SlotPatchReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.Slot
import java.util.*

interface SlotPort {

    @Throws
    fun create(eventId: Int, req: SlotCreationReq): Slot
    fun getById(id: UUID): Slot
    fun getProgram(eventId: Int): Map<Int, Map<Hall, List<Slot>>>
    fun update(id: UUID, request: SlotPatchReq): Slot
    fun remove(id: UUID)
    @Throws
    fun associateHall(id: UUID, eventId: Int, hallId: Int): Slot
    fun dissociateHall(id: UUID, hallId: Int)

}