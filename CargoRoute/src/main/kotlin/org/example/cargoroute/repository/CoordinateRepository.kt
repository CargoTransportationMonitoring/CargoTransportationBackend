package org.example.cargoroute.repository

import org.example.cargoroute.entity.CoordinateEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CoordinateRepository : JpaRepository<CoordinateEntity, Long> {

    fun deleteByRouteId(routeId: String)

    fun findByRouteId(routeId: String): List<CoordinateEntity>

    @Query(
        "SELECT DISTINCT c.route_id FROM coordinate c",
        countQuery = "SELECT COUNT(DISTINCT c.route_id) FROM coordinate c",
        nativeQuery = true
    )
    fun findDistinctRouteIds(pageable: Pageable): Page<String>
}