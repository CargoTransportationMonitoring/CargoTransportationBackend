package org.example.cargotrackingservice.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.cargotrackingservice.entity.GeoLocationEntity
import java.time.LocalDateTime

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val time: LocalDateTime?,
    val email: String,
) {
    fun toEntity(): GeoLocationEntity {
        return GeoLocationEntity(
            latitude = latitude,
            longitude = longitude,
            time = time
        )
    }
}