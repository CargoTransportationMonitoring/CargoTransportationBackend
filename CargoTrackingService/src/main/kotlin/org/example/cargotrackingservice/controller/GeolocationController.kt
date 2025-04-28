package org.example.cargotrackingservice.controller

import org.example.cargotrackingservice.dto.GeoLocation
import org.example.cargotrackingservice.service.GeoLocationRedisService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class GeolocationController(
    private val geoLocationRedisService: GeoLocationRedisService
) {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    fun receiveMessage(geoLocation: GeoLocation) {
        geoLocationRedisService.saveLocation(geoLocation.email, geoLocation.toEntity())
    }
}
