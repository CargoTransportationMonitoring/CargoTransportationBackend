package org.example.cargotransporationmonitoring.cargo.service.impl

import org.example.cargotransporationmonitoring.messages.CargoMessages.CARGO_NOT_FOUND
import org.example.cargotransporationmonitoring.cargo.model.CargoEntity
import org.example.cargotransporationmonitoring.cargo.repository.CargoRepository
import org.example.cargotransporationmonitoring.cargo.service.CargoService
import org.example.cargotransporationmonitoring.util.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class CargoServiceImpl(
    private val cargoRepository: CargoRepository
) : CargoService {

    override fun getCargo(id: Long): CargoEntity {
        return cargoRepository.findByCargoId(id)
            ?: throw EntityNotFoundException(CARGO_NOT_FOUND, id.toString())
    }
}