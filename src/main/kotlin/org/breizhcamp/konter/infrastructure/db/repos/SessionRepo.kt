package org.breizhcamp.konter.infrastructure.db.repos

import org.breizhcamp.konter.infrastructure.db.model.SessionDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SessionRepo: JpaRepository<SessionDB, Int>, SessionRepoCustom {

    @Modifying
    @Query("""
        UPDATE SessionDB session SET session.barcode = ?2 WHERE session.id = ?1
    """)
    fun addBarcode(id: Int, barcode: String)

    @Modifying
    @Query("""
        UPDATE slot SET session_id = ? WHERE id = ?
    """, nativeQuery = true)
    fun setSlot(id: Int, slotId: UUID)

    @Query("""
        SELECT slot.id FROM SlotDB slot WHERE slot.barcode = ?1
    """)
    fun getSlotIdByBarcode(barcode: String): UUID

}