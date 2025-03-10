package org.example.cargocommon.security.keycloak

import org.example.cargocommon.dto.CommonRegisterUserRequest
import org.example.cargocommon.dto.CommonRoles
import org.example.cargocommon.dto.CommonUpdateUserRequest
import org.keycloak.representations.idm.UserRepresentation

interface KeycloakService {

    /**
     * Create a new user in Keycloak
     */
    fun createKeyCloakUser(request: CommonRegisterUserRequest): String

    /**
     * Add roles to a user in Keycloak
     */
    fun addRolesToUser(userId: String, roles: List<CommonRoles>)

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
    fun updateKeyCloakUser(userId: String, updateUserRequest: CommonUpdateUserRequest)

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

    /**
     * get userId by username
     */
    fun getUserIdByUsername(username: String): String?
}