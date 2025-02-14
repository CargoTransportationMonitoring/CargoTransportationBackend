package org.example.cargoroute.service

import com.example.model.route.CreateRouteRequest
import com.example.model.route.GetRouteResponse
import com.example.model.route.MarkPointsRequest
import com.example.model.route.PaginationResponse
import com.example.model.route.RouteExistResponse

interface RouteService {

    /**
     * Create a new route
     */
    fun createRoute(route: CreateRouteRequest): GetRouteResponse

    /**
     * Find a route by id
     */
    fun findById(routeId: Long): GetRouteResponse

    /**
     * Update Route
     */
    fun updateRoute(routeId: Long, createRouteRequest: CreateRouteRequest): GetRouteResponse

    /**
     * Delete a route
     */
    fun deleteRoute(routeId: Long)

    /**
     * Find all routes
     */
    fun findPagingWithFilter(username: String?, routeStatus: String?, pointsNumberFrom: Long?,
                             pointsNumberTo: Long?, description: String?, routeName: String?): PaginationResponse

    /**
     * Change points state
     */
    fun markPoints(routeId: Long, markPointsRequest: MarkPointsRequest): GetRouteResponse

    /**
     * Check if user has routes
     */
    fun isRouteExistByUser(userId: String): RouteExistResponse
}