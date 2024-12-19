package org.example.cargotransporationmonitoring.cargo.model

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class CargoEntity(
    @Id
    val cargoId: Long?,
    val name: String,
    val description: String?,
    val weight: Long,
    val status: CargoStatus,
    val createdAt: LocalDateTime?,
    val vehicleId: Int?
)
