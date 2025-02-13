package org.example.cargotransporationmonitoring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class CargoTransporationMonitoringApplication

fun main(args: Array<String>) {
    runApplication<CargoTransporationMonitoringApplication>(*args)
}
