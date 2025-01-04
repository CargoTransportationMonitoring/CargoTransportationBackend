package org.example.cargotransporationmonitoring.users.service.impl

import com.example.model.users.LinkUserRequest
import org.example.cargotransporationmonitoring.security.keycloak.KeycloakService
import org.example.cargotransporationmonitoring.users.repository.UserAdminRepository
import org.example.cargotransporationmonitoring.users.service.UserService
import org.example.cargotransporationmonitoring.util.attachment.TokenUtil
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val tokenUtil: TokenUtil,
    private val keycloakService: KeycloakService,
    private val userAdminRepository: UserAdminRepository
) : UserService {

    companion object {
        private const val MINUTES_EXPIRATION = 5L
    }

    override fun linkUser(linkUserRequest: LinkUserRequest): String {
        val userAdmin = tokenUtil.decryptToken(linkUserRequest.code)
        userAdminRepository.save(userAdmin)
        return keycloakService.getUsernameByUserId(userAdmin.adminId)
    }

    override fun unlinkUser(userId: String) {
        userAdminRepository.deleteByUserId(userId)
    }

    override fun generateCode(username: String, adminId: String): String {
        val userId = keycloakService.getUserIdByUsername(username)
        require(userId != null) { "User not found" }
        return tokenUtil.generateToken(userId, adminId, MINUTES_EXPIRATION)
    }

    override fun getUsersByAdmin(adminId: String): List<String> {
        val userIds = userAdminRepository.findByAdminId(adminId).map { it.userId }
        return userIds.asSequence().map { keycloakService.getUsernameByUserId(it) }.toList()
    }
}