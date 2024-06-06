package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.springframework.stereotype.Service

@Service
class HallSetOrder(
    private val hallPort: HallPort
) {

    fun setOrder(id: Int, eventId: Int, order: Int?) = hallPort.setOrderInEvent(id, eventId, order)

}