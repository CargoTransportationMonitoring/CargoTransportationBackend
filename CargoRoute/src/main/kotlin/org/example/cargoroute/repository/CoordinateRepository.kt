package org.example.cargoroute.repository

import org.example.cargoroute.entity.CoordinateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CoordinateRepository : JpaRepository<CoordinateEntity, Long> {

    fun deleteByRouteId(routeId: Long)

    fun findByRouteId(routeId: Long): List<CoordinateEntity>
}