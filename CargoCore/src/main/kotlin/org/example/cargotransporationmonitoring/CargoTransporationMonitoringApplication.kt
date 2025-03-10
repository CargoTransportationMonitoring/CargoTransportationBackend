package org.example.cargotransporationmonitoring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = ["org.example.cargotransporationmonitoring", "org.example.cargocommon"])
class CargoTransporationMonitoringApplication

fun main(args: Array<String>) {
    runApplication<CargoTransporationMonitoringApplication>(*args)
}
