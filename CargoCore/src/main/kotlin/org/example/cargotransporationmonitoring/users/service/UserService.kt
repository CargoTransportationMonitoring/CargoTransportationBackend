package org.example.cargotransporationmonitoring.users.service

import com.example.model.users.LinkUserRequest

interface UserService {
    /**
     * Link User to Administrator
     */
    fun linkUser(linkUserRequest: LinkUserRequest): String

    /**
     * Unlink user from Administrator
     */
    fun unlinkUser(userId: String)

    /**
     * Generate personal code for link
     */
    fun generateCode(username: String, adminId: String): String

    /**
     * Get all users by adminId
     */
    fun getUsersByAdmin(adminId: String): List<String>
}