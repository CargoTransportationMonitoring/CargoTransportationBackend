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
    fun deleteRoute(routeId: Long)

    /**
     * Find a route by id
     */
    fun findById(routeId: Long): GetRouteResponse

    /**
     * Find all routes
     */
    fun findPaging(page: Int, size: Int): PaginationResponse

    /**
     * Find all routes by user
     */
    fun findPaging(page: Int, size: Int, userId: String): PaginationResponse

    /**
     * Update Route
     */
    fun updateRoute(routeId: Long, createRouteRequest: CreateRouteRequest): GetRouteResponse
}