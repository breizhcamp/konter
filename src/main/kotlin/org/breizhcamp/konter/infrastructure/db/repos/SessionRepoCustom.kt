package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.infrastructure.db.model.SessionDB

interface SessionRepoCustom {

    fun filter(eventId: Int, filter: SessionFilter, sortByFormat: Boolean): List<SessionDB>

}