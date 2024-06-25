package org.breizhcamp.konter.infrastructure.db

import jakarta.transaction.Transactional
import org.breizhcamp.konter.application.requests.HallCreationReq
import org.breizhcamp.konter.application.requests.HallPatchReq
import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.breizhcamp.konter.infrastructure.db.mappers.toHall
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.springframework.stereotype.Component

@Component
class HallAdapter (
    private val hallRepo: HallRepo
) : HallPort{
    override fun get(id: Int): Hall =
        hallRepo.findById(id).get().toHall()

    override fun list(eventId: Int?): List<Hall> {
        eventId?.let {
            return hallRepo.getAllByAvailableEventId(eventId)
                .map { it.toHall() }
        }
        return hallRepo.findAll().map { it.toHall() }
    }

    @Transactional
    override fun create(req: HallCreationReq): Hall {
        val id = hallRepo.create(req.name)
        if (req.trackId != null) {
            hallRepo.addTrackIdToHall(id, req.trackId)
        }
        return hallRepo.findById(id).get().toHall()
    }

    @Transactional
    override fun associateToEvent(id: Int, eventId: Int, order: Int): Hall {
        hallRepo.associateToEvent(id, eventId, order)
        return hallRepo.findById(id).get().toHall()
    }

    @Transactional
    override fun dissociateFromEvent(id: Int, eventId: Int): Hall {
        val hallsAfter = hallRepo.getAllByOrderAfterHallInEvent(eventId, id)
        hallRepo.dissociateFromEvent(id, eventId)
        hallsAfter.forEach { hallRepo.decreaseOrder(it.id, eventId) }
        return hallRepo.findById(id).get().toHall()
    }

    @Transactional
    override fun setOrderInEvent(id: Int, eventId: Int, order: Int): Hall {
        hallRepo.setOrderInEvent(id, eventId, order)
        return hallRepo.findById(id).get().toHall()
    }

    @Transactional
    override fun update(id: Int, req: HallPatchReq): Hall {
        val hall = hallRepo.findById(id).get().copy(name = req.name, trackId = req.trackId)
        return hallRepo.save(hall).toHall()
    }

    @Transactional
    override fun delete(id: Int) = hallRepo.deleteById(id)

}