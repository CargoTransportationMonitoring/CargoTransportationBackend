package org.example.cargoroute.controller

import com.example.api.route.RouteApi
import com.example.model.route.CreateRouteRequest
import com.example.model.route.GetRouteResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
class RouteController : RouteApi {

    @PreAuthorize("hasRole('admin')")
    override fun apiV1RoutesPost(createRouteRequest: CreateRouteRequest): ResponseEntity<GetRouteResponse> {
        println(createRouteRequest)
        return ResponseEntity.ok(GetRouteResponse())
    }
}
