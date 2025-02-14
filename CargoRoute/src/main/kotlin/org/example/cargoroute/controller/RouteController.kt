package org.example.cargoroute.controller

import com.example.api.route.RouteApi
import com.example.model.route.CreateRouteRequest
import com.example.model.route.GetRouteResponse
import com.example.model.route.MarkPointsRequest
import com.example.model.route.PaginationResponse
import com.example.model.route.RouteExistResponse
import org.example.cargoroute.keycloak.KeycloakService
import org.example.cargoroute.service.RouteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
class RouteController(
    private val routeService: RouteService,
    private val keycloakService: KeycloakService
) : RouteApi {

    @PreAuthorize("hasRole('admin')")
    override fun apiV1RoutePost(createRouteRequest: CreateRouteRequest): ResponseEntity<GetRouteResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(createRouteRequest))
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    override fun apiV1RouteRouteIdGet(routeId: Long): ResponseEntity<GetRouteResponse> {
        routeService.findById(routeId).let {
            return ResponseEntity.ok().body(it)
        }
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1RouteRouteIdDelete(routeId: Long): ResponseEntity<Void> {
        routeService.deleteRoute(routeId)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasAnyRole('admin')")
    override fun apiV1RoutesGet(
        username: String?,
        routeStatus: String,
        pointsNumberFrom: Long?,
        pointsNumberTo: Long?,
        description: String?,
        routeName: String?
    ): ResponseEntity<PaginationResponse> {
        val response = routeService.findPagingWithFilter(
            username, routeStatus, pointsNumberFrom,
            pointsNumberTo, description, routeName
        )
        return ResponseEntity.ok().body(response)
    }

    @PreAuthorize("hasRole('admin') or #userId == authentication.name")
    override fun apiV1UserUserIdRoutesGet(
        userId: String, routeStatus: String, pointsNumberFrom: Long?,
        pointsNumberTo: Long?, description: String?, routeName: String?
    ): ResponseEntity<PaginationResponse> {
        val username = keycloakService.getUsernameByUserId(userId)
        val response = routeService.findPagingWithFilter(
            username, routeStatus, pointsNumberFrom,
            pointsNumberTo, description, routeName
        )
        return ResponseEntity.ok().body(response)
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1RouteRouteIdPut(
        routeId: Long,
        createRouteRequest: CreateRouteRequest
    ): ResponseEntity<GetRouteResponse> {
        routeService.updateRoute(routeId, createRouteRequest).let {
            return ResponseEntity.ok().body(it)
        }
    }

    @PreAuthorize("hasRole('user')")
    override fun apiV1RouteRouteIdMarkPointsPut(
        routeId: Long,
        markPointsRequest: MarkPointsRequest
    ): ResponseEntity<GetRouteResponse> {
        return ResponseEntity.ok().body(routeService.markPoints(routeId, markPointsRequest))
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    override fun apiV1UserUserIdRoutesExistGet(userId: String): ResponseEntity<RouteExistResponse> {
        return ResponseEntity.ok().body(routeService.isRouteExistByUser(userId))
    }
}
