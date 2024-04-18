package org.breizhcamp.konter.infrastructure.db.repos

import com.querydsl.jpa.JPAExpressions
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.infrastructure.db.model.QSessionDB
import org.breizhcamp.konter.infrastructure.db.model.QSpeakerDB
import org.breizhcamp.konter.infrastructure.db.model.SessionDB
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class SessionRepoCustomImpl: QuerydslRepositorySupport(SessionDB::class.java), SessionRepoCustom {
    override fun filter(year: Int, filter: SessionFilter): List<SessionDB> {
        val session = QSessionDB.sessionDB
        val speaker = QSpeakerDB.speakerDB
        val query = from(session)

        query.where(session.event.year.eq(year))

        filter.id?.let {
            query.where(session.id.eq(it))
        }

        filter.title?.let {
            query.where(session.title.containsIgnoreCase(it))
        }

        filter.speakerName?.let {
            query.where(session.speakers.contains(
                JPAExpressions.selectFrom(speaker)
                    .where(speaker.firstname.concat(" ")
                        .concat(speaker.lastname)
                        .containsIgnoreCase(it))
            ))
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

        return query.fetch()
    }
}