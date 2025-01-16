package org.example.cargoroute.entity

import jakarta.persistence.*


@Entity
@Table(name = "coordinate")
class CoordinateEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coordinate_seq_gen")
    @SequenceGenerator(
        name = "coordinate_seq_gen",
        sequenceName = "coordinate_seq",
        allocationSize = 1
    )
    val id: Long?,

    @Column(name = "latitude")
    val latitude: Double,

    @Column(name = "longitude")
    val longitude: Double,

    @Column(name = "route_id")
    val routeId: Long,

    @Column(name = "is_visited")
    var isVisited: Boolean,

    @Column(name = "\"order\"")
    val order: Int
) {
    data class Builder(
        var id: Long? = null,
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var routeId: Long = 0,
        var isVisited: Boolean = false,
        var order: Int = 0
    ) {
        fun build(): CoordinateEntity {
            return CoordinateEntity(id, latitude, longitude, routeId, isVisited, order)
        }
    }
}