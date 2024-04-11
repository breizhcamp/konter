package org.breizhcamp.konter.domain.entities.enums

enum class SessionFormatEnum(val sessionizeFormat: Regex) {
    CONFERENCE(Regex("Conf.rence \\(55 min\\)")),
    UNIVERSITY(Regex("Universit. \\(2h, Mercredi\\)")),
    TOOL(Regex("Tool in action \\(25 min\\)")),
    QUICKY(Regex("Quicky \\(15 min\\)")),
    KEYNOTE(Regex("Keynote"));

    companion object {
        fun getFromString(string: String): SessionFormatEnum =
            entries.first { it.sessionizeFormat.matches(string) }

        fun getLabel(formatEnum: SessionFormatEnum) = formatEnum.sessionizeFormat.pattern
            .replace(", Mercredi", "")
            .replace("\\", "")
            .replace(".", "é")
    }
}

fun SessionFormatEnum.getLabel() = SessionFormatEnum.getLabel(formatEnum = this)

