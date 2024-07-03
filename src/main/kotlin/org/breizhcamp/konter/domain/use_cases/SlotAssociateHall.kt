package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.entities.Slot
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class SlotAssociateHall (
    private val slotPort: SlotPort
) {

    fun associate(id: UUID, eventId: Int, hallId: Int): Slot = slotPort.associateHall(id, eventId, hallId)
    fun dissociate(id: UUID, hallId: Int) = slotPort.dissociateHall(id, hallId)

}