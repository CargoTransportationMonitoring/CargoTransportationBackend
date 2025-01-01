package org.example.cargotransporationmonitoring.users

import com.example.api.users.UserApi
import com.example.model.users.GetUserResponse
import com.example.model.users.LinkUserRequest
import com.example.model.users.RegisterUserRequest
import com.example.model.users.UpdateUserRequest
import org.example.cargotransporationmonitoring.security.keycloak.KeycloakService
import org.keycloak.admin.client.CreatedResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val keycloakService: KeycloakService
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
    override fun apiV1UsersGet(): ResponseEntity<List<GetUserResponse>> {
        return ResponseEntity.ok(listOf(GetUserResponse()))
    }

    @PreAuthorize("hasRole('admin')")
    override fun apiV1UserLinkPost(linkUserRequest: LinkUserRequest): ResponseEntity<Void> {
        keycloakService.linkUserToAdmin(linkUserRequest)
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/api/v1/user/unlink/{userId}")
    fun unlinkUserFromAdmin(@PathVariable userId: String, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Void> {
        keycloakService.unlinkUserFromAdmin(userId)
        return ResponseEntity.ok().build()
    }
}