package org.breizhcamp.konter.infrastructure.db

import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.breizhcamp.konter.infrastructure.db.mappers.toHall
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.springframework.stereotype.Component

@Component
class HallAdapter (
    private val hallRepo: HallRepo
) : HallPort{
    override fun list(eventId: Int?): List<Hall> {
        eventId?.let {
            return hallRepo.getAllByAvailableEventId(eventId)
                .map { it.toHall() }
        }
        return hallRepo.findAll().map { it.toHall() }
    }

    override fun create(req: HallCreationReq): Hall {
        val id = hallRepo.create(req.name)
        return hallRepo.findById(id).get().toHall()
    }

}