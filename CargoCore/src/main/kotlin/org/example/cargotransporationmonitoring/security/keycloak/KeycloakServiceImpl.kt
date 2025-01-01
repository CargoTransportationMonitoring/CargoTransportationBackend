package org.example.cargotransporationmonitoring.security.keycloak

import com.example.model.users.LinkUserRequest
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

    companion object {
        private const val ADMIN_ID = "adminId"
    }

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

    override fun linkUserToAdmin(linkUserRequest: LinkUserRequest) {
        val userResource = usersResource[linkUserRequest.userId]
        val userRepresentation = userResource.toRepresentation()

        val attributes = userRepresentation.attributes ?: mutableMapOf()
        require(!attributes.containsKey(ADMIN_ID)) { "User is already linked to an admin" }

        attributes[ADMIN_ID] = listOf(linkUserRequest.adminId)
        userRepresentation.attributes = attributes

        userResource.update(userRepresentation)
    }

    override fun unlinkUserFromAdmin(userId: String) {
        val userResource = usersResource[userId]
        val userRepresentation = userResource.toRepresentation()

        val attributes = userRepresentation.attributes ?: mutableMapOf()

        attributes.remove(ADMIN_ID)
        userRepresentation.attributes = attributes

        userResource.update(userRepresentation)
    }


    private fun createPassword(password: String): CredentialRepresentation {
        return CredentialRepresentation().apply {
            this.isTemporary = false
            this.type = CredentialRepresentation.PASSWORD
            this.value = password
        }
    }
}