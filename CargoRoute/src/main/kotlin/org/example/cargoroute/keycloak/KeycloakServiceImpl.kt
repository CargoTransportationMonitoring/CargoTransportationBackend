package org.example.cargoroute.keycloak

import jakarta.annotation.PostConstruct
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UsersResource
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
}