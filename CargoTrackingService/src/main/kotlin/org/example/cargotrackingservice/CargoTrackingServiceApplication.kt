package org.example.cargotrackingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CargoTrackingServiceApplication

fun main(args: Array<String>) {
	runApplication<CargoTrackingServiceApplication>(*args)
}
