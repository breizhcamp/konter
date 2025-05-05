package org.breizhcamp.konter.domain.entities.enums

enum class SessionFormatEnum(val sessionizeFormat: Regex, val label: String) {
    CONFERENCE(Regex("Conf.rence \\(55 min\\)"), "Conférence"),
    UNIVERSITY(Regex("Universit. \\(2h"), "Université"),
    TOOL(Regex("Tool in action \\(25 min\\)"), "Tool in action"),
    QUICKY(Regex("Quicky \\(15 min\\)"), "Quicky"),
    KEYNOTE(Regex("Keynote"), "Keynote"),;

    companion object {
        fun getFromString(string: String): SessionFormatEnum =
            entries.firstOrNull { it.sessionizeFormat.matches(string) }
                ?: throw IllegalArgumentException("Unknown session format: $string")

        fun getLabel(formatEnum: SessionFormatEnum) = formatEnum.label
    }
}

fun SessionFormatEnum.getLabel() = SessionFormatEnum.getLabel(formatEnum = this)

