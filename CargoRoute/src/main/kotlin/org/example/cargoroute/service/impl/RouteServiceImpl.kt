package org.example.cargoroute.service.impl

import com.example.model.route.*
import jakarta.persistence.EntityManager
import org.example.cargoroute.client.CoreClient
import org.example.cargoroute.entity.CoordinateEntity
import org.example.cargoroute.entity.RouteEntity
import org.example.cargoroute.entity.RouteItemEntity
import org.example.cargoroute.entity.RouteStatus
import org.example.cargoroute.exception.ResourceNotFoundException
import org.example.cargoroute.keycloak.KeycloakService
import org.example.cargoroute.mapper.CoordinatesMapper.coordinatesToEntity
import org.example.cargoroute.repository.CoordinateRepository
import org.example.cargoroute.repository.RouteRepository
import org.example.cargoroute.service.RouteService
import org.example.cargoroute.util.EnumFinder
import org.example.cargoroute.util.ErrorMessages.COMPLETED_STATUS_SET_NOT_VISITED_POINTS_YET_ERROR
import org.example.cargoroute.util.ErrorMessages.NEW_STATUS_SET_ALREADY_VISITED_POINTS_ERROR
import org.example.cargoroute.util.ErrorMessages.ROUTE_NOT_NEW_DELETE_ERROR
import org.example.cargoroute.util.ErrorMessages.ROUTE_NOT_FOUND_ERROR
import org.example.cargoroute.util.ErrorMessages.ROUTE_NOT_NEW_EDIT_ERROR
import org.example.cargoroute.util.ErrorMessages.USER_NOT_BELONG_ADMIN_ERROR
import org.example.cargoroute.util.ErrorMessages.USER_NOT_FOUND_ERROR
import org.example.cargoroute.util.SecurityUtil.getCurrentUserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

@Service
class RouteServiceImpl(
    private val coordinateRepository: CoordinateRepository,
    private val routeRepository: RouteRepository,
    private val entityManager: EntityManager,
    private val keycloakService: KeycloakService,
    private val coreClient: CoreClient
) : RouteService {

    @Transactional
    override fun createRoute(route: CreateRouteRequest): GetRouteResponse {
        if (StringUtils.hasLength(route.assignedUsername)) {
            checkUserBelongToAdmin(route.assignedUsername)
        }
        val routeEntity = RouteEntity.Builder().apply {
            name = route.name
            description = route.description
            assignedUsername = route.assignedUsername
            routeStatus = RouteStatus.NEW.name
            adminUsername = keycloakService.getUsernameByUserId(getCurrentUserId())
        }.build()
        val savedRoute = routeRepository.save(routeEntity)
        val coordinates = coordinatesToEntity(route.coordinates, savedRoute.id!!)
        return buildGetRouteResponse(coordinateRepository.saveAll(coordinates), savedRoute)
    }

    @Transactional
    override fun deleteRoute(routeId: Long) {
        val routeEntity = routeRepository.findById(routeId)
            .orElseThrow { ResourceNotFoundException(ROUTE_NOT_FOUND_ERROR.format(routeId)) }
        require(RouteStatus.NEW.name == routeEntity.routeStatus) { ROUTE_NOT_NEW_DELETE_ERROR.format(routeId) }

        coordinateRepository.removeByRouteId(routeId)
        entityManager.flush()
        routeRepository.deleteById(routeId)
    }

    override fun findById(routeId: Long): GetRouteResponse {
        val routeEntity = routeRepository.findById(routeId)
            .orElseThrow { ResourceNotFoundException(ROUTE_NOT_FOUND_ERROR.format(routeId)) }

        return buildGetRouteResponse(coordinateRepository.findByRouteId(routeId), routeEntity)
    }

    override fun findPagingWithFilter(
        username: String?, routeStatus: String?,
        pointsNumberFrom: Long?, pointsNumberTo: Long?, description: String?, routeName: String?
    ): PaginationResponse {
        val routeStatusEnum = EnumFinder.findByName(RouteStatus::class.java, routeStatus)
        val routeItemPage = routeRepository.findWithFilter(
            PageRequest.of(0, 5), //TODO сделать keyset пагинацию
            username ?: "",
            description ?: "",
            routeStatusEnum?.name ?: RouteStatus.NEW.name,
            pointsNumberFrom,
            pointsNumberTo,
            keycloakService.getUsernameByUserId(getCurrentUserId()),
            routeName ?: ""
        )
        return buildPaginationResponse(routeItemPage)
    }

    @Transactional
    override fun updateRoute(routeId: Long, createRouteRequest: CreateRouteRequest): GetRouteResponse {
        val routeEntity = routeRepository.findById(routeId)
            .orElseThrow { ResourceNotFoundException(ROUTE_NOT_FOUND_ERROR.format(routeId)) }
        require(RouteStatus.NEW.name == routeEntity.routeStatus) { ROUTE_NOT_NEW_EDIT_ERROR.format(routeId) }

        coordinateRepository.removeByRouteId(routeId)
        entityManager.flush()
        val assignedUsername = if (StringUtils.hasLength(createRouteRequest.assignedUsername)) {
            checkUserBelongToAdmin(createRouteRequest.assignedUsername)
            createRouteRequest.assignedUsername
        } else {
            ""
        }
        routeRepository.updateRouteEntity(
            routeId,
            createRouteRequest.name,
            createRouteRequest.description,
            assignedUsername,
            null
        )
        val coordinates = coordinatesToEntity(createRouteRequest.coordinates, routeId)
        return buildGetRouteResponse(coordinateRepository.saveAll(coordinates), routeRepository.findById(routeId).get())
    }

    @Transactional
    override fun markPoints(
        routeId: Long,
        markPointsRequest: MarkPointsRequest
    ): GetRouteResponse {
        val coordinatesMap: Map<Long, CoordinateEntity> =
            coordinateRepository.findByRouteId(routeId).associateBy { it.id!! }
        markPointsRequest.coordinates.forEach { coordinatesMap[it.id]?.isVisited = it.isVisited }
        coordinateRepository.saveAll(coordinatesMap.values)
        updateRouteEntityStatus(routeId, markPointsRequest)
        return findById(routeId)
    }

    override fun isRouteExistByUser(userId: String): RouteExistResponse {
        val username = keycloakService.getUsernameByUserId(userId)
        val response = RouteExistResponse()
        return response.isUserExist(routeRepository.existsByAssignedUsername(username))
    }

    private fun buildPaginationResponse(routeEntityPage: Page<RouteItemEntity>): PaginationResponse {
        return PaginationResponse().apply {
            content = routeItemToDto(routeEntityPage.content)
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

    private fun routeItemToDto(routeItemEntityList: List<RouteItemEntity>): List<RouteItem> {
        return routeItemEntityList.map { routeItemEntity ->
            RouteItem().apply {
                id = routeItemEntity.id
                name = routeItemEntity.name
                description = routeItemEntity.description
                assignedUsername = routeItemEntity.assignedUsername
                routeStatus = RouteItem.RouteStatusEnum.fromValue(routeItemEntity.routeStatus.name)
                pointsCount = routeItemEntity.pointsCount
            }
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
                CoordinateItem().apply {
                    id = coordinate.id
                    latitude = coordinate.latitude.toFloat()
                    longitude = coordinate.longitude.toFloat()
                    isVisited = coordinate.isVisited
                }
            }
            this.name = routeEntity.name
            this.description = routeEntity.description
            this.assignedUsername = routeEntity.assignedUsername
            this.routeStatus = routeEntity.routeStatus
        }
    }

    private fun checkUserBelongToAdmin(username: String) {
        val userId = keycloakService.getUserIdByUsername(username)
            ?: throw ResourceNotFoundException(USER_NOT_FOUND_ERROR.format(username))
        val adminId = getCurrentUserId()

        require(coreClient.isUserBelongToAdmin(userId, adminId)) {
            USER_NOT_BELONG_ADMIN_ERROR.format(userId, adminId)
        }
    }

    private fun updateRouteEntityStatus(routeId: Long, markPointsRequest: MarkPointsRequest) {
        if (RouteStatus.COMPLETED.name == markPointsRequest.routeStatus) {
            markPointsRequest.coordinates.forEach {
                require(it.isVisited != false) { COMPLETED_STATUS_SET_NOT_VISITED_POINTS_YET_ERROR }
            }
        }
        if (RouteStatus.NEW.name == markPointsRequest.routeStatus) {
            markPointsRequest.coordinates.forEach {
                require(it.isVisited != true) { NEW_STATUS_SET_ALREADY_VISITED_POINTS_ERROR }
            }
        }
        val routeEntityOptional = routeRepository.findById(routeId)
        if (routeEntityOptional.isPresent) {
            val routeEntity = routeEntityOptional.get()
            routeEntity.routeStatus = markPointsRequest.routeStatus
            routeRepository.save(routeEntity)
        }
    }
}
