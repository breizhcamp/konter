package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class SlotSetSession (
    private val sessionPort: SessionPort
) {
    fun setById(id: Int, slotId: UUID) = sessionPort.setSlotById(id, slotId)
    fun setByBarcode(id: Int, barcode: String) = sessionPort.setSlotByBarcode(id, barcode)
}