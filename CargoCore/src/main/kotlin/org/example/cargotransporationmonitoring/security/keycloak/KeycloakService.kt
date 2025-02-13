package org.example.cargotransporationmonitoring.security.keycloak

import com.example.model.users.RegisterUserRequest
import com.example.model.users.UpdateUserRequest
import org.example.cargotransporationmonitoring.entity.Roles
import org.keycloak.representations.idm.UserRepresentation

interface KeycloakService {

    /**
     * Create a new user in Keycloak
     */
    fun createKeyCloakUser(request: RegisterUserRequest): String

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
    fun updateKeyCloakUser(userId: String, updateUserRequest: UpdateUserRequest)

    /**
     * Unlink a user from a group in Keycloak
     */
    fun unlinkUserFromAdmin(userId: String)

    /**
     * get userId by username
     */
    fun tryGetUserIdByUsername(username: String): String?

    /**
     * get username by userId
     */
    fun getUsernameByUserId(userId: String): String
}