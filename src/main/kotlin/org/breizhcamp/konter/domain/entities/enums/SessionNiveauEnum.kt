package org.breizhcamp.konter.domain.entities.enums

enum class SessionNiveauEnum(val sessionizeNiveau: Regex) {
    INTRO(Regex("Introduction")),
    STANDARD(Regex("Standard")),
    ADVANCED(Regex("Avanc."));

    companion object {
        fun getFromString(string: String) =
            entries.first { it.sessionizeNiveau.matches(string) }
    }
}