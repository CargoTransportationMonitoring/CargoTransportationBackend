package org.example.cargoroute.entity

import org.example.cargocommon.util.EnumFinder

class RouteItemEntity(
    val id: Long,
    val name: String,
    val description: String,
    val assignedUsername: String,
    val routeStatus: RouteStatus,
    val pointsCount: Int
) {
    constructor(
        id: Long,
        name: String,
        description: String,
        assignedUsername: String,
        routeStatus: String,
        pointsCount: Long
    ) : this(
        id = id,
        name = name,
        description = description,
        assignedUsername = assignedUsername,
        routeStatus = EnumFinder.findByName(RouteStatus::class.java, routeStatus) ?: RouteStatus.NEW,
        pointsCount = pointsCount.toInt()
    )
}
