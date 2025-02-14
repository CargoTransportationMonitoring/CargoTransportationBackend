package org.example.cargoroute

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class CargoRouteApplication

fun main(args: Array<String>) {
    runApplication<CargoRouteApplication>(*args)
}
