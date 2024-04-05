package org.breizhcamp.konter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KonterApplication

fun main(args: Array<String>) {
	runApplication<KonterApplication>(*args)
}
