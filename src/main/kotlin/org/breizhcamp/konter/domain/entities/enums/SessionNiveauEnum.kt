package org.breizhcamp.konter.domain.entities.enums

enum class SessionNiveauEnum(val sessionizeNiveau: Regex, val label: String) {
    INTRO(Regex("Introduction"), "Introduction"),
    STANDARD(Regex("Standard"), "Standard"),
    ADVANCED(Regex("Avanc."), "Avancé"),;

    companion object {
        fun getFromString(string: String) =
            entries.first { it.sessionizeNiveau.matches(string) }
    }
}
