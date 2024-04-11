package org.breizhcamp.konter.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "konter")
data class KonterConfig(
    val kalon: KalonConfig
)

data class KalonConfig(
    val enabled: Boolean,
    val url: String,

    val secured: Boolean,
    val apiKey: String?,
)
