package org.breizhcamp.konter.infrastructure.db.repos

import mu.KotlinLogging
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.infrastructure.db.model.QSessionDB
import org.breizhcamp.konter.infrastructure.db.model.QSpeakerDB
import org.breizhcamp.konter.infrastructure.db.model.SessionDB
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

private val logger = KotlinLogging.logger {  }

class SessionRepoCustomImpl: QuerydslRepositorySupport(SessionDB::class.java), SessionRepoCustom {
    override fun filter(year: Int, filter: SessionFilter): List<SessionDB> {
        val session = QSessionDB.sessionDB
        val speaker = QSpeakerDB.speakerDB
        val query = from(session)

        query.where(session.event.year.eq(year))

        filter.id?.let {
            query.where(session.id.eq(it))
            val bypassedFilterValue = query.fetch()
            if (bypassedFilterValue.size == 1) {
                logger.info { "Session found with id, bypassing the rest of the filter" }
                return bypassedFilterValue
            }
        }

        filter.title?.let {
            query.where(session.title.containsIgnoreCase(it))
        }

        filter.speakerName?.let {
            val speakers = from(speaker)
                .where(speaker.firstname.concat(" ")
                    .concat(speaker.lastname)
                    .containsIgnoreCase(it))
                .fetch()

            if (speakers.size > 0) {
                var subContains = session.speakers.contains(speakers.removeFirst())
                speakers.forEach {
                    speaker -> subContains = subContains
                        .or(session.speakers.contains(speaker))
                }
                query.where(subContains)
            }
        }

        filter.format?.let {
            query.where(session.format.eq(it))
        }

        filter.theme?.let {
            query.where(session.theme.eq(it))
        }

        filter.niveau?.let {
            query.where(session.niveau.eq(it))
        }

        filter.status?.let {
            query.where(session.status.eq(it))
        }

        filter.rated?.let {
            if (it) {
                query.where(session.rating.isNotNull)
            } else {
                query.where(session.rating.isNull)
            }
        }

        query.orderBy(session.rating.desc().nullsLast())

        return query.fetch()
    }
}