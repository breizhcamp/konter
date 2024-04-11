package org.breizhcamp.konter.domain.entities.enums

enum class SessionThemeEnum(val sessionizeTheme: Regex) {
    WEB(Regex("Web")),
    METHODS(Regex("M.thodologie")),
    DATA(Regex("Data")),
    ARCHI(Regex("Architecture")),
    DEV(Regex("D.veloppement")),
    IOT(Regex("IoT Embarqu.")),
    MOBILE(Regex("Mobile")),
    AI(Regex("IA")),
    GREEN(Regex(".coconception")),
    DEVOPS(Regex("DevOps")),
    SEC(Regex("S.curit.")),
    OTHER(Regex("Autre\\.\\.\\.")),
    KEYNOTE(Regex("Keynote"));

    companion object {
        fun getFromString(string: String): SessionThemeEnum =
            entries.first { it.sessionizeTheme.matches(string) }

        fun getLabel(themeEnum: SessionThemeEnum): String {
            return when (themeEnum) {
                OTHER -> "Autre..."
                GREEN -> "Écoconception"
                else -> themeEnum.sessionizeTheme.pattern.replace(".", "é")
            }
        }
    }
}

fun SessionThemeEnum.getLabel() = SessionThemeEnum.getLabel(themeEnum = this)