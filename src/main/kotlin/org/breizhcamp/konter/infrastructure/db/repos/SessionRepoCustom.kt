package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.infrastructure.db.model.SessionDB
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SessionRepoCustom {

    fun filter(eventId: Int, filter: SessionFilter, sortByFormat: Boolean, page: Pageable): Page<SessionDB>

}