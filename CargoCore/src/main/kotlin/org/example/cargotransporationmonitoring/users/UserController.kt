package org.example.cargotransporationmonitoring.users

import com.example.api.users.UserApi
import com.example.model.users.GetUserResponse
import com.example.model.users.LinkUserRequest
import com.example.model.users.RegisterUserRequest
import com.example.model.users.UpdateUserRequest
import org.example.cargotransporationmonitoring.security.keycloak.KeycloakService
import org.example.cargotransporationmonitoring.users.service.UserService
import org.keycloak.admin.client.CreatedResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val keycloakService: KeycloakService,
    private val userService: UserService
) : UserApi, CustomUserApi {

    companion object {
        private val logger = LoggerFactory.getLogger(UserController::class.java)
    }

    override fun apiV1UserRegisterPost(registerUserRequest: RegisterUserRequest): ResponseEntity<GetUserResponse> {
        val response = keycloakService.createKeyCloakUser(registerUserRequest)
        if (response.status == HttpStatus.CONFLICT.value()) {
            return ResponseEntity(HttpStatus.CONFLICT)
        }
        val userId = CreatedResponseUtil.getCreatedId(response)
        keycloakService.addRolesToUser(userId, listOf(Roles.USER))

        logger.info("User with email ${registerUserRequest.email} has been created")
        return ResponseEntity.status(201).body(
            GetUserResponse()
                .id(userId)
                .username(registerUserRequest.username)
        )
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    override fun apiV1UserUserIdGet(userId: String): ResponseEntity<GetUserResponse> {
        val user = keycloakService.findKeyCloakUserById(userId)
        return ResponseEntity.ok(
            GetUserResponse()
                .id(user.id)
                .username(user.username)
        )
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    override fun apiV1UserUserIdDelete(userId: String): ResponseEntity<Void> {
        keycloakService.deleteKeyCloakUserById(userId)
        userService.unlinkUser(userId)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    override fun apiV1UserUserIdPut(
        userId: String,
        updateUserRequest: UpdateUserRequest,
    ): ResponseEntity<GetUserResponse> {
        if (userId != updateUserRequest.userId) {
            return ResponseEntity.badRequest().build()
        }
        keycloakService.updateKeyCloakUser(updateUserRequest)
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1UsersGet(): ResponseEntity<List<String>>? {
        return ResponseEntity.ok().body(userService.getUsersByAdmin(getCurrentUserId()))
    }

    @PreAuthorize("hasRole('user')")
    @PutMapping("/api/v1/user/link")
    fun apiV1UserLinkPost(@RequestBody linkUserRequest: LinkUserRequest): ResponseEntity<String> {
        return ResponseEntity.ok().body(userService.linkUser(linkUserRequest))
    }

    @PreAuthorize("hasRole('user')")
    @PutMapping("/api/v1/user/unlink/{userId}")
    fun unlinkUserFromAdmin(@PathVariable userId: String): ResponseEntity<Void> {
        userService.unlinkUser(userId)
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/api/v1/user/generateCode")
    fun generateLinkCode(@RequestParam username: String): ResponseEntity<String> {
        val adminId = getCurrentUserId()
        return ResponseEntity.ok().body(userService.generateCode(username, adminId))
    }

    private fun getCurrentUserId(): String {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        return jwt.claims["sub"] as? String
            ?: throw IllegalArgumentException("Subject (sub) not found in JWT")
    }
}