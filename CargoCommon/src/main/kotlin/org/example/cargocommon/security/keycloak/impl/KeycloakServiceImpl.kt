package org.example.cargocommon.security.keycloak.impl

import jakarta.annotation.PostConstruct
import org.example.cargocommon.dto.CommonRegisterUserRequest
import org.example.cargocommon.dto.CommonRoles
import org.example.cargocommon.dto.CommonUpdateUserRequest
import org.example.cargocommon.exception.ResourceAlreadyExistException
import org.example.cargocommon.security.keycloak.KeycloakService
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class KeycloakServiceImpl : KeycloakService {

    @Value("\${keycloak.auth-server-url}")
    private lateinit var serverUrl: String

    @Value("\${keycloak.realm}")
    private lateinit var realm: String

    @Value("\${keycloak.resource}")
    private lateinit var clientId: String

    @Value("\${keycloak.credentials.secret}")
    private lateinit var clientSecret: String

    private lateinit var keycloak: Keycloak
    private lateinit var realmResource: RealmResource
    private lateinit var usersResource: UsersResource

    @PostConstruct
    fun init() {
        keycloak = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .build()

        realmResource = keycloak.realm(realm)
        usersResource = realmResource.users()
    }

    override fun createKeyCloakUser(request: CommonRegisterUserRequest): String {
        val credentialRepresentation = createPassword(request.password)
        val kcUser = UserRepresentation().apply {
            username = request.username
            email = request.email
            isEnabled = true
            isEmailVerified = false
            credentials = listOf(credentialRepresentation)
        }
        val response = usersResource.create(kcUser)
        require(response.status != HttpStatus.CONFLICT.value()) {
            throw ResourceAlreadyExistException(
                ErrorMessages.USER_ALREADY_EXIST_ERROR.format(request.email, request.username)
            )
        }
        return CreatedResponseUtil.getCreatedId(response)
    }

    override fun addRolesToUser(userId: String, roles: List<CommonRoles>) {
        val kcRoles = roles.map { role -> realmResource.roles()[role.roleName].toRepresentation() }.toList()
        val uniqueUserResource = usersResource[userId]
        uniqueUserResource.roles().realmLevel().add(kcRoles)
    }

    override fun deleteKeyCloakUserById(userId: String) {
        usersResource[userId].remove()
    }

    override fun findKeyCloakUserById(userId: String): UserRepresentation {
        return usersResource[userId].toRepresentation()
    }

    override fun updateKeyCloakUser(userId: String, updateUserRequest: CommonUpdateUserRequest) {
        val kcUser = UserRepresentation().apply {
            firstName = updateUserRequest.name
            lastName = updateUserRequest.surname
        }
        val uniqueUsersResource = usersResource[userId]
        uniqueUsersResource.update(kcUser)
    }

    override fun unlinkUserFromAdmin(userId: String) {
        val kcUser = UserRepresentation().apply {
            federationLink = null
        }
        val uniqueUsersResource = usersResource[userId]
        uniqueUsersResource.update(kcUser)
    }

    override fun tryGetUserIdByUsername(username: String): String? {
        val users = realmResource.users()
        val userSearchResults = users.search(username)
        val userRepresentation = userSearchResults.firstOrNull { it.username == username }
        return userRepresentation?.id
    }

    override fun getUsernameByUserId(userId: String): String {
        val userResource = usersResource[userId]
        return userResource.toRepresentation().username
    }

    override fun getUserIdByUsername(username: String): String? {
        val users = realmResource.users()
        val userSearchResults = users.search(username)
        val userRepresentation = userSearchResults.firstOrNull { it.username == username }
        return userRepresentation?.id
    }

    private fun createPassword(password: String): CredentialRepresentation {
        return CredentialRepresentation().apply {
            this.isTemporary = false
            this.type = CredentialRepresentation.PASSWORD
            this.value = password
        }
    }
}