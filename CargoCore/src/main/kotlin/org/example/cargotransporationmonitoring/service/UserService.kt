package org.example.cargotransporationmonitoring.service

import com.example.model.users.GetUserDetailsResponse
import com.example.model.users.GetUserResponse
import com.example.model.users.LinkUserRequest
import com.example.model.users.RegisterUserRequest
import com.example.model.users.UpdateUserRequest

interface UserService {

    /**
     * Register new user
     */
    fun registerUser(registerUserRequest: RegisterUserRequest): GetUserResponse

    /**
     * Get user
     */
    fun getUser(userId: String): GetUserResponse

    /**
     * Update user
     */
    fun updateUser(userId: String, updateUserRequest: UpdateUserRequest): GetUserResponse

    /**
     * Delete user
     */
    fun deleteUser(userId: String)

    /**
     * Link User to Administrator
     */
    fun linkUser(userId: String, linkUserRequest: LinkUserRequest)

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
    fun getUsersByAdmin(adminId: String): List<GetUserResponse>

    /**
     * Get user details
     */
    fun getUserDetails(username: String): GetUserDetailsResponse

    /**
     * Check user belong admin
     */
    fun checkUserBelongAdmin(userId: String, adminId: String): Boolean
}