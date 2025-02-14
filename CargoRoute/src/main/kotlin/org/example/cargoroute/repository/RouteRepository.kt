package org.example.cargoroute.repository

import org.example.cargoroute.entity.RouteEntity
import org.example.cargoroute.entity.RouteItemEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RouteRepository : JpaRepository<RouteEntity, Long> {
    @Query(
        """
    SELECT new org.example.cargoroute.entity.RouteItemEntity(
        r.id,
        r.name,
        r.description,
        r.assignedUsername,
        r.routeStatus,
        COUNT(c.id)
    )
    FROM RouteEntity r
    LEFT JOIN CoordinateEntity c ON c.routeId = r.id
    WHERE (:username = '' OR r.assignedUsername LIKE %:username%) 
      AND (:routeStatus IS NULL OR r.routeStatus = :routeStatus)
      AND (:description = '' OR r.description LIKE %:description%)
      AND (:adminUsername = '' OR r.adminUsername = :adminUsername)
      AND (:routeName = '' OR r.name LIKE %:routeName%)
    GROUP BY r.id, r.name, r.description, r.assignedUsername, r.routeStatus
    HAVING (:pointsNumberFrom IS NULL OR COALESCE(COUNT(c.id), 0) >= :pointsNumberFrom)
       AND (:pointsNumberTo IS NULL OR COALESCE(COUNT(c.id), 0) <= :pointsNumberTo)
    """
    )
    fun findWithFilter(
        pageable: Pageable,
        @Param("username") username: String,
        @Param("description") description: String,
        @Param("routeStatus") routeStatus: String?,
        @Param("pointsNumberFrom") pointsNumberFrom: Long?,
        @Param("pointsNumberTo") pointsNumberTo: Long?,
        @Param("adminUsername") adminUsername: String?,
        @Param("routeName") routeName: String?
    ): Page<RouteItemEntity>

    fun existsByAssignedUsername(assignedUsername: String): Boolean


    @Modifying
    @Query(
        """
        UPDATE RouteEntity r SET 
        r.name = COALESCE(:name, r.name),
        r.description = COALESCE(:description, r.description),
        r.assignedUsername = COALESCE(:assignedUsername, r.assignedUsername),
        r.routeStatus = COALESCE(:routeStatus, r.routeStatus)
        WHERE r.id = :routeId
        """
    )
    fun updateRouteEntity(
        @Param("routeId") routeId: Long,
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("assignedUsername") assignedUsername: String?,
        @Param("routeStatus") routeStatus: String?
    )

}