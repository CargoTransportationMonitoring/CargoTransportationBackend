package org.example.cargoeureka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class CargoEurekaApplication

fun main(args: Array<String>) {
	runApplication<CargoEurekaApplication>(*args)
}
