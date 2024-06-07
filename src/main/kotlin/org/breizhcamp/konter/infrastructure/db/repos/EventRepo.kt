package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.EventDB
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepo: JpaRepository<EventDB, Int>