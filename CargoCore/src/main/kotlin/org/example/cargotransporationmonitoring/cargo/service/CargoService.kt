package org.example.cargotransporationmonitoring.cargo.service

import org.example.cargotransporationmonitoring.cargo.model.CargoEntity

interface CargoService {

    fun getCargo(id: Long): CargoEntity
}