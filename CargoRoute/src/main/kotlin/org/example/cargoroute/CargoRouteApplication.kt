package org.example.cargoroute

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = ["org.example.cargoroute", "org.example.cargocommon"])
class CargoRouteApplication

fun main(args: Array<String>) {
    runApplication<CargoRouteApplication>(*args)
}
