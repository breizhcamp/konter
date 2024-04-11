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

        fun getLabel(themeEnum: SessionThemeEnum): String =
            themeEnum.sessionizeTheme.replace(".", "é")
    }
}