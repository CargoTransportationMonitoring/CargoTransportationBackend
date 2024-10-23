package org.example.cargotransporationmonitoring.cargo.repository

import org.example.cargotransporationmonitoring.cargo.model.CargoEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface CargoRepository: CrudRepository<CargoEntity, Long> {

    @Query("SELECT * from cargo where cargo_id = :id")
    fun findByCargoId(id: Long): CargoEntity?
}