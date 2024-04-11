package org.breizhcamp.konter.domain.entities

data class SpeakerFilter(
    val lastname: String?,
    val firstname: String?,
    val email: String?,
) {
    companion object {
        fun empty() = SpeakerFilter(null, null, null)
    }
}
