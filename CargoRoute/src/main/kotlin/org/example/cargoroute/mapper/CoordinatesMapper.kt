package org.example.cargoroute.mapper

import com.example.model.route.CoordinateItem
import org.example.cargoroute.entity.CoordinateEntity

object CoordinatesMapper {

    fun coordinatesToEntity(coordinates: List<CoordinateItem>, routeId: Long): List<CoordinateEntity> {
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
}