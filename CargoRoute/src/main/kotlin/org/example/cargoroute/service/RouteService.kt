package org.example.cargoroute.service

import com.example.model.route.CreateRouteRequest
import com.example.model.route.GetRouteResponse
import com.example.model.route.PaginationResponse

interface RouteService {

    /**
     * Create a new route
     */
    fun createRoute(route: CreateRouteRequest): GetRouteResponse

    /**
     * Delete a route
     */
    fun deleteRoute(routeId: String)

    /**
     * Find a route by id
     */
    fun findById(routeId: String): GetRouteResponse

    /**
     * Find all routes
     */
    fun findPaging(page: Int, size: Int): PaginationResponse

    /**
     * Update Route
     */
    fun updateRoute(routeId: String, createRouteRequest: CreateRouteRequest): GetRouteResponse
}