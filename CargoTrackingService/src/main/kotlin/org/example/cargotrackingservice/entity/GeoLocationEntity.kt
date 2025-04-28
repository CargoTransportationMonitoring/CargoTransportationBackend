package org.example.cargotrackingservice.entity

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.cargotrackingservice.dto.GeoLocation
import java.time.LocalDateTime

data class GeoLocationEntity(
    val latitude: Double,
    val longitude: Double,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val time: LocalDateTime?,
) {
    fun toDto(email: String): GeoLocation {
        return GeoLocation(
            latitude = latitude,
            longitude = longitude,
            time = time,
            email = email
        )
    }
}