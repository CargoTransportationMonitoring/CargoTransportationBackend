package org.example.cargoroute.entity

import jakarta.persistence.*

@Entity
@Table(name = "route")
class RouteEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_seq_gen")
    @SequenceGenerator(
        name = "route_seq_gen",
        sequenceName = "route_seq",
        allocationSize = 1
    )
    val id: Long?,

    @Column(name = "name")
    val name: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "assigned_username")
    val assignedUsername: String? = null,

    @Column(name = "route_status")
    var routeStatus: String,

    @Column(name = "admin_username")
    val adminUsername: String
) {
    data class Builder(
        var id: Long? = null,
        var name: String = "",
        var description: String = "",
        var assignedUsername: String? = null,
        var routeStatus: String = RouteStatus.NEW.name,
        var adminUsername: String = ""
    ) {
        fun build(): RouteEntity {
            return RouteEntity(id, name, description, assignedUsername, routeStatus, adminUsername)
        }
    }

}