package org.breizhcamp.konter

import org.breizhcamp.konter.config.KonterConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(KonterConfig::class)
class KonterApplication

fun main(args: Array<String>) {
	runApplication<KonterApplication>(*args)
}
