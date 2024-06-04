package org.breizhcamp.konter.application.requests

import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Duration
import java.time.LocalTime

data class SlotCreationReq(
    val start: LocalTime,
    val day: Int,
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    val duration: Duration,
)
