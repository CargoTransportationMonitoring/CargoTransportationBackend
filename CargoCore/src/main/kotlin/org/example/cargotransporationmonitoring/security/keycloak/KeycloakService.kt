package org.example.cargotransporationmonitoring.security.keycloak

import com.example.model.users.RegisterUserRequest
import com.example.model.users.UpdateUserRequest
import jakarta.ws.rs.core.Response
import org.example.cargotransporationmonitoring.users.Roles
import org.keycloak.representations.idm.UserRepresentation

interface KeycloakService {

    /**
     * Create a new user in Keycloak
     */
    fun createKeyCloakUser(request: RegisterUserRequest): Response

    /**
     * Add roles to a user in Keycloak
     */
    fun addRolesToUser(userId: String, roles: List<Roles>)

    /**
     * Delete a user in Keycloak
     */
    fun deleteKeyCloakUserById(userId: String)

    /**
     * Find a user in Keycloak by id
     */
    fun findKeyCloakUserById(userId: String): UserRepresentation

    /**
     * Update a user in Keycloak
     */
    fun updateKeyCloakUser(updateUserRequest: UpdateUserRequest)

    /**
     * Link a user to a group in Keycloak
     */
    fun linkUserToAdmin(userId: String, adminId: String)

    /**
     * Unlink a user from a group in Keycloak
     */
    fun unlinkUserFromAdmin(userId: String)

    /**
     * get userId by username
     */
    fun getUserIdByUsername(username: String): String?

    fun getUsernameByUserId(userId: String): String
}