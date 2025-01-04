package org.example.cargotransporationmonitoring.security.keycloak

import com.example.model.users.RegisterUserRequest
import com.example.model.users.UpdateUserRequest
import jakarta.annotation.PostConstruct
import jakarta.ws.rs.core.Response
import org.example.cargotransporationmonitoring.users.Roles
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
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

    override fun createKeyCloakUser(request: RegisterUserRequest): Response {
        val credentialRepresentation = createPassword(request.password)
        val kcUser = UserRepresentation().apply {
            username = request.username
            email = request.email
            isEnabled = true
            isEmailVerified = false
            credentials = listOf(credentialRepresentation)
        }
        return usersResource.create(kcUser)
    }


    override fun addRolesToUser(userId: String, roles: List<Roles>) {
        val kcRoles = roles.map { role -> realmResource.roles()[role.getRoleName()].toRepresentation() }.toList()
        val uniqueUserResource = usersResource[userId]
        uniqueUserResource.roles().realmLevel().add(kcRoles)
    }

    override fun deleteKeyCloakUserById(userId: String) {
        usersResource[userId].remove()
    }

    override fun findKeyCloakUserById(userId: String): UserRepresentation {
        return usersResource[userId].toRepresentation()
    }

    override fun updateKeyCloakUser(updateUserRequest: UpdateUserRequest) {
        val kcUser = UserRepresentation().apply {
            firstName = updateUserRequest.name
            lastName = updateUserRequest.surname
        }
        val uniqueUsersResource = usersResource[updateUserRequest.userId]
        uniqueUsersResource.update(kcUser)
    }

    override fun linkUserToAdmin(userId: String, adminId: String) {
        checkUserAlreadyLinked(userId)
        val kcUser = UserRepresentation().apply {
            federationLink = adminId
        }
        val uniqueUsersResource = usersResource[userId]
        uniqueUsersResource.update(kcUser)
    }

    private fun checkUserAlreadyLinked(userId: String) {
        val userResource = realmResource.users()[userId]
        val userRepresentation = userResource.toRepresentation()
        require(userRepresentation.federationLink.isNullOrBlank()) { "User is already linked to an admin" }
    }

    override fun unlinkUserFromAdmin(userId: String) {
        val kcUser = UserRepresentation().apply {
            federationLink = null
        }
        val uniqueUsersResource = usersResource[userId]
        uniqueUsersResource.update(kcUser)
    }

    override fun getUserIdByUsername(username: String): String? {
        val users = realmResource.users()
        val userSearchResults = users.search(username)
        val userRepresentation = userSearchResults.firstOrNull { it.username == username }
        return userRepresentation?.id
    }

    override fun getUsernameByUserId(userId: String): String {
        val userResource = usersResource[userId]
        return userResource.toRepresentation().username
    }

    private fun createPassword(password: String): CredentialRepresentation {
        return CredentialRepresentation().apply {
            this.isTemporary = false
            this.type = CredentialRepresentation.PASSWORD
            this.value = password
        }
    }
}