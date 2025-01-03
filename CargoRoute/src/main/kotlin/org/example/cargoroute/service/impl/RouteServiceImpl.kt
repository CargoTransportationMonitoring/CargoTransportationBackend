package org.example.cargoroute.service.impl

import com.example.model.route.*
import jakarta.persistence.EntityManager
import org.example.cargoroute.entity.CoordinateEntity
import org.example.cargoroute.repository.CoordinateRepository
import org.example.cargoroute.service.RouteService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RouteServiceImpl(
    private val coordinateRepository: CoordinateRepository,
    private val entityManager: EntityManager
) : RouteService {

    override fun createRoute(route: CreateRouteRequest): GetRouteResponse {
        val coordinates = coordinatesToEntity(route.coordinates)
        return buildGetRouteResponse(coordinateRepository.saveAll(coordinates))
    }

    @Transactional
    override fun deleteRoute(routeId: String) {
        coordinateRepository.deleteByRouteId(routeId)
    }

    override fun findById(routeId: String): GetRouteResponse {
        return buildGetRouteResponse(coordinateRepository.findByRouteId(routeId))
    }

    override fun findPaging(page: Int, size: Int): PaginationResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        val routeIdsPage: Page<String> = coordinateRepository.findDistinctRouteIds(pageable)
        return PaginationResponse().apply {
            content = routeIdsPage.content
            totalPages = routeIdsPage.totalPages
            totalElements = routeIdsPage.totalElements.toInt()
            last = routeIdsPage.isLast
            this.size = routeIdsPage.size
            number = routeIdsPage.number
            numberOfElements = routeIdsPage.numberOfElements
            first = routeIdsPage.isFirst
            empty = routeIdsPage.isEmpty
        }
    }

    @Transactional
    override fun updateRoute(routeId: String, createRouteRequest: CreateRouteRequest): GetRouteResponse {
        coordinateRepository.deleteByRouteId(routeId)
        entityManager.flush()
        val coordinates = coordinatesToEntity(createRouteRequest.coordinates, routeId)
        return buildGetRouteResponse(coordinateRepository.saveAll(coordinates))
    }

    private fun buildGetRouteResponse(coordinates: List<CoordinateEntity>): GetRouteResponse {
        if (coordinates.isEmpty()) {
            return GetRouteResponse()
        }
        val sortedCoordinates = coordinates.sortedBy { it.order }

        return GetRouteResponse().apply {
            this.id = sortedCoordinates.first().routeId
            this.coordinates = sortedCoordinates.map { coordinate ->
                GetRouteResponseCoordinatesInner().apply {
                    latitude = coordinate.latitude.toFloat()
                    longitude = coordinate.longitude.toFloat()
                    isVisited = coordinate.isVisited
                }
            }
        }
    }

    private fun coordinatesToEntity(
        coordinates: List<CreateRouteRequestCoordinatesInner>,
        routeId: String? = null
    ): List<CoordinateEntity> {
        val generatedRouteId = routeId.takeIf { !it.isNullOrBlank() } ?: UUID.randomUUID().toString()
        return coordinates.mapIndexed { index, coordinate ->
            CoordinateEntity.Builder().apply {
                latitude = coordinate.latitude.toDouble()
                longitude = coordinate.longitude.toDouble()
                this.routeId = generatedRouteId
                isVisited = false
                order = index
            }.build()
        }
    }
}
