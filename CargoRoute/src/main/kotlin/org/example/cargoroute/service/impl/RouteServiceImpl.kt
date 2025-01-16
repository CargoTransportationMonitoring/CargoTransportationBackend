package org.example.cargoroute.service.impl

import com.example.model.route.*
import jakarta.persistence.EntityManager
import org.example.cargoroute.entity.CoordinateEntity
import org.example.cargoroute.entity.RouteEntity
import org.example.cargoroute.keycloak.KeycloakService
import org.example.cargoroute.keycloak.KeycloakServiceImpl
import org.example.cargoroute.repository.CoordinateRepository
import org.example.cargoroute.repository.RouteRepository
import org.example.cargoroute.service.RouteService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RouteServiceImpl(
    private val coordinateRepository: CoordinateRepository,
    private val routeRepository: RouteRepository,
    private val entityManager: EntityManager,
    private val keycloakService: KeycloakService
) : RouteService {

    @Transactional
    override fun createRoute(route: CreateRouteRequest): GetRouteResponse {
        if (route.assignedUsername != null) {
            checkUserBelongToAdmin(route.assignedUsername)
        }
        val routeEntity = RouteEntity.Builder().apply {
            name = route.name
            description = route.description
            assignedUsername = route.assignedUsername
        }.build()
        val savedRoute = routeRepository.save(routeEntity)
        val coordinates = coordinatesToEntity(route.coordinates, savedRoute.id!!)
        return buildGetRouteResponse(coordinateRepository.saveAll(coordinates), savedRoute)
    }

    @Transactional
    override fun deleteRoute(routeId: Long) {
        coordinateRepository.deleteByRouteId(routeId)
        entityManager.flush()
        routeRepository.deleteById(routeId)
    }

    override fun findById(routeId: Long): GetRouteResponse {
        val routeEntity = routeRepository.findById(routeId)
        if (routeEntity.isEmpty) {
            return GetRouteResponse()
        }
        return buildGetRouteResponse(coordinateRepository.findByRouteId(routeId), routeEntity.get())
    }

    override fun findPaging(page: Int, size: Int): PaginationResponse {
        val routeEntityPage = routeRepository.findAll(PageRequest.of(page, size))
        return buildPaginationResponse(routeEntityPage)
    }

    override fun findPaging(page: Int, size: Int, userId: String): PaginationResponse {
        val username = keycloakService.getUsernameByUserId(userId)
        val routeEntityPage = routeRepository.findByAssignedUsername(username, PageRequest.of(page, size))
        return buildPaginationResponse(routeEntityPage)
    }

    @Transactional
    override fun updateRoute(routeId: Long, createRouteRequest: CreateRouteRequest): GetRouteResponse {
        coordinateRepository.deleteByRouteId(routeId)
        entityManager.flush()
        if (createRouteRequest.assignedUsername != null) {
            checkUserBelongToAdmin(createRouteRequest.assignedUsername)
        }
        val routeEntity = RouteEntity.Builder().apply {
            id = routeId
            name = createRouteRequest.name
            description = createRouteRequest.description
            assignedUsername = createRouteRequest.assignedUsername
        }.build()
        val updatedRouteEntity = routeRepository.save(routeEntity)

        val coordinates = coordinatesToEntity(createRouteRequest.coordinates, routeId)
        return buildGetRouteResponse(coordinateRepository.saveAll(coordinates), updatedRouteEntity)
    }

    override fun markPoints(
        routeId: Long,
        points: List<ApiV1RoutesMarkPointsRouteIdPutRequestInner>
    ): GetRouteResponse {
        val coordinatesMap: Map<Long, CoordinateEntity> = coordinateRepository.findByRouteId(routeId).associateBy { it.id!! }
        points.forEach { coordinatesMap[it.id]?.isVisited = it.isVisited }
        coordinateRepository.saveAll(coordinatesMap.values)
        return findById(routeId)
    }

    private fun buildPaginationResponse(routeEntityPage: Page<RouteEntity>): PaginationResponse {
        return PaginationResponse().apply {
            content = routeEntityPage.content.map { it.id }
            totalPages = routeEntityPage.totalPages
            totalElements = routeEntityPage.totalElements.toInt()
            last = routeEntityPage.isLast
            this.size = routeEntityPage.size
            number = routeEntityPage.number
            numberOfElements = routeEntityPage.numberOfElements
            first = routeEntityPage.isFirst
            empty = routeEntityPage.isEmpty
        }
    }

    private fun buildGetRouteResponse(coordinates: List<CoordinateEntity>, routeEntity: RouteEntity): GetRouteResponse {
        if (coordinates.isEmpty()) {
            return GetRouteResponse()
        }
        val sortedCoordinates = coordinates.sortedBy { it.order }

        return GetRouteResponse().apply {
            this.id = sortedCoordinates.first().routeId
            this.coordinates = sortedCoordinates.map { coordinate ->
                ApiV1RoutesMarkPointsRouteIdPutRequestInner().apply {
                    id = coordinate.id
                    latitude = coordinate.latitude.toFloat()
                    longitude = coordinate.longitude.toFloat()
                    isVisited = coordinate.isVisited
                }
            }
            this.name = routeEntity.name
            this.description = routeEntity.description
            this.assignedUsername = routeEntity.assignedUsername
        }
    }

    private fun coordinatesToEntity(
        coordinates: List<CreateRouteRequestCoordinatesInner>,
        routeId: Long
    ): List<CoordinateEntity> {
        return coordinates.mapIndexed { index, coordinate ->
            CoordinateEntity.Builder().apply {
                latitude = coordinate.latitude.toDouble()
                longitude = coordinate.longitude.toDouble()
                this.routeId = routeId
                isVisited = false
                order = index
            }.build()
        }
    }

    private fun checkUserBelongToAdmin(username: String) {
        val adminId = getCurrentUserId()
        // TODO: Implement the logic to check if the user belongs to the admin
    }

    private fun getCurrentUserId(): String {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        return jwt.claims["sub"] as? String
            ?: throw IllegalArgumentException("Subject (sub) not found in JWT")
    }
}
