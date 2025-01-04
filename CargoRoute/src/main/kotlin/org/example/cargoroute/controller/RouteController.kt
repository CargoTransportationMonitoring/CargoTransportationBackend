package org.example.cargoroute.controller

import com.example.api.route.DefaultApi
import com.example.api.route.RouteApi
import com.example.model.route.CreateRouteRequest
import com.example.model.route.GetRouteResponse
import com.example.model.route.PaginationResponse
import org.example.cargoroute.service.RouteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.context.request.NativeWebRequest
import java.util.*

@Controller
class RouteController(
    private val routeService: RouteService
) : RouteApi, DefaultApi {

    override fun getRequest(): Optional<NativeWebRequest> {
        return Optional.empty()
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1RoutesPost(createRouteRequest: CreateRouteRequest): ResponseEntity<GetRouteResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(createRouteRequest))
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1RoutesRouteIdDelete(routeId: Long): ResponseEntity<Void> {
        routeService.deleteRoute(routeId)
        return ResponseEntity.noContent().build()
    }

    override fun apiV1RoutesRouteIdGet(routeId: Long): ResponseEntity<GetRouteResponse> {
        routeService.findById(routeId).let {
            return ResponseEntity.ok().body(it)
        }
    }

    @PreAuthorize("hasAnyRole('admin')")
    override fun apiV1RoutesGet(page: Int, size: Int): ResponseEntity<PaginationResponse> {
        return ResponseEntity.ok().body(routeService.findPaging(page, size))
    }

    @PreAuthorize("hasRole('admin') or #userId == authentication.name")
    override fun apiV1RoutesUserUserIdGet(userId: String, page: Int, size: Int): ResponseEntity<PaginationResponse> {
        return ResponseEntity.ok().body(routeService.findPaging(page, size, userId))
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1RoutesRouteIdPut(routeId: Long, createRouteRequest: CreateRouteRequest): ResponseEntity<GetRouteResponse> {
        routeService.updateRoute(routeId, createRouteRequest).let {
            return ResponseEntity.ok().body(it)
        }
    }

    private fun isAdmin(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val authorities = authentication.authorities
        return authorities.contains(SimpleGrantedAuthority("ROLE_admin"))
    }
}
