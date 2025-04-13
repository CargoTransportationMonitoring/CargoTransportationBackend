package org.example.cargotransporationmonitoring.controller

import com.example.api.users.UserApi
import com.example.model.users.*
import org.example.cargotransporationmonitoring.service.UserService
import org.example.cargocommon.util.SecurityUtils.getCurrentUserId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService
) : UserApi {

    override fun apiV1UserPost(registerUserRequest: RegisterUserRequest): ResponseEntity<GetUserResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(registerUserRequest))
    }

    @PreAuthorize("hasAnyRole('admin') or #userId == authentication.name")
    override fun apiV1UserUserIdGet(userId: String): ResponseEntity<GetUserResponse> {
        return ResponseEntity.ok(userService.getUser(userId))
    }

    @PreAuthorize("#userId == authentication.name")
    override fun apiV1UserUserIdPut(
        userId: String,
        updateUserRequest: UpdateUserRequest
    ): ResponseEntity<GetUserResponse> {
        return ResponseEntity.ok(userService.updateUser(userId, updateUserRequest))
    }

    @PreAuthorize("hasRole('user') and #userId == authentication.name")
    override fun apiV1UserUserIdDelete(userId: String): ResponseEntity<Void> {
        userService.deleteUser(userId)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1UsersGet(): ResponseEntity<List<GetUserResponse>> {
        val adminId = getCurrentUserId()
        return ResponseEntity.ok().body(userService.getUsersByAdmin(adminId))
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1UserUsernameDetailsGet(username: String): ResponseEntity<GetUserDetailsResponse> {
        return ResponseEntity.ok().body(userService.getUserDetails(username))
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1CheckAdminMembershipGet(userId: String, adminId: String): ResponseEntity<CheckAdminResponse> {
        return ResponseEntity.ok().body(userService.checkUserBelongAdmin(userId, adminId))
    }

    @PreAuthorize("hasRole('user') and #userId == authentication.name")
    override fun apiV1UserUserIdLinkPut(userId: String, linkUserRequest: LinkUserRequest): ResponseEntity<Void> {
        userService.linkUser(userId, linkUserRequest)
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('user') and #userId == authentication.name")
    override fun apiV1UserUserIdUnlinkPut(userId: String): ResponseEntity<Void> {
        userService.unlinkUser(userId)
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1UserGenerateCodeGet(username: String): ResponseEntity<CodeGeneratedResponse> {
        val adminId = getCurrentUserId()
        return ResponseEntity.ok().body(userService.generateCode(username, adminId))
    }
}