package org.example.cargoroute.keycloak

interface KeycloakService {

    fun getUserIdByUsername(username: String): String?

    fun getUsernameByUserId(userId: String): String
}