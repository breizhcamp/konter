package org.breizhcamp.konter.domain.entities

import java.math.BigDecimal

data class Evaluation(
    val session: Session,
    val rating: BigDecimal,
)
