package org.breizhcamp.konter.domain.entities.enums

enum class SessionStatusEnum(val sessionizeStatus: String) {
    ACCEPTED("Accepted"),
    ACCEPT_QUEUE("Accept Queue"),
    NOMINATED("Nominated"),
    DECLINE_QUEUE("Decline Queue"),
    DECLINED("Declined");

    companion object {
        fun getFromString(string: String) =
            entries.first { it.sessionizeStatus == string }
    }
}