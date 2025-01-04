package org.example.cargoroute.repository

import org.example.cargoroute.entity.RouteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RouteRepository: JpaRepository<RouteEntity, Long> {

//    @Query("SELECT r FROM RouteEntity r WHERE r.assignedUsername = :username")
    fun findByAssignedUsername(username: String, pageable: Pageable): Page<RouteEntity>
}