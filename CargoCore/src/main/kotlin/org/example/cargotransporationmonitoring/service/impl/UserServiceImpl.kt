package org.example.cargotransporationmonitoring.service.impl

import com.example.model.users.*
import org.example.cargocommon.dto.CommonRoles
import org.example.cargotransporationmonitoring.client.RouteClient
import org.example.cargotransporationmonitoring.exception.LinkCodeInvalidException
import org.example.cargocommon.exception.ResourceAlreadyExistException
import org.example.cargocommon.exception.ResourceNotFoundException
import org.example.cargotransporationmonitoring.exception.UserHasRoutesException
import org.example.cargocommon.security.keycloak.KeycloakService
import org.example.cargotransporationmonitoring.repository.UserAdminRepository
import org.example.cargotransporationmonitoring.service.UserService
import org.example.cargotransporationmonitoring.util.ErrorMessages.LINK_CODE_CONTAINS_NOT_VALID_USERNAME
import org.example.cargotransporationmonitoring.util.ErrorMessages.USER_ALREADY_LINKED_ERROR
import org.example.cargotransporationmonitoring.util.ErrorMessages.USER_HAS_ROUTES_ERROR
import org.example.cargotransporationmonitoring.util.ErrorMessages.USER_NOT_FOUND_ERROR
import org.example.cargotransporationmonitoring.util.LinkTokenUtil
import org.example.cargotransporationmonitoring.util.toCommonRegisterUserRequest
import org.example.cargotransporationmonitoring.util.toCommonUpdateUserRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val linkTokenUtil: LinkTokenUtil,
    private val keycloakService: KeycloakService,
    private val userAdminRepository: UserAdminRepository,
    private val routeClient: RouteClient
) : UserService {

    companion object {
        private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

        private const val MINUTES_EXPIRATION = 5L
    }

    override fun registerUser(registerUserRequest: RegisterUserRequest): GetUserResponse {
        val userId = keycloakService.createKeyCloakUser(toCommonRegisterUserRequest(registerUserRequest))
        keycloakService.addRolesToUser(userId, listOf(CommonRoles.USER))

        logger.info("User with email ${registerUserRequest.email} has been successfully created")
        return GetUserResponse()
            .id(userId)
            .username(registerUserRequest.username)
    }

    override fun getUser(userId: String): GetUserResponse {
        val user = keycloakService.findKeyCloakUserById(userId)
        val adminUsername = tryGetAdminUsernameByUserId(userId)
        return GetUserResponse()
            .id(user.id)
            .username(user.username)
            .adminUsername(adminUsername)
    }

    override fun updateUser(userId: String, updateUserRequest: UpdateUserRequest): GetUserResponse {
        keycloakService.updateKeyCloakUser(userId, toCommonUpdateUserRequest(updateUserRequest))
        return getUser(userId)
    }

    override fun deleteUser(userId: String) {
        require(!routeClient.isExistByUserId(userId).isUserExist) {
            throw UserHasRoutesException(USER_HAS_ROUTES_ERROR.format(userId))
        }
        keycloakService.deleteKeyCloakUserById(userId)
        unlinkUser(userId)
        logger.info("User with id: $userId was successfully deleted")
    }

    override fun linkUser(userId: String, linkUserRequest: LinkUserRequest) {
        val userAdmin = linkTokenUtil.decryptToken(linkUserRequest.code)
        require(userAdmin.userId == userId) {
            throw LinkCodeInvalidException(LINK_CODE_CONTAINS_NOT_VALID_USERNAME.format(userId))
        }
        require(userAdminRepository.findByUserId(userId) == null) {
            throw ResourceAlreadyExistException(USER_ALREADY_LINKED_ERROR.format())
        }
        // require(userAdmin.adminId) TODO необходимо добавить проверку на роль, что adminId действительно id пользователя с ролью администратор
        userAdminRepository.save(userAdmin)
        logger.info("User with id: $userId was successfully linked to admin with id: $userAdmin")
    }

    override fun unlinkUser(userId: String) {
        require(!routeClient.isExistByUserId(userId).isUserExist) {
            throw UserHasRoutesException(USER_HAS_ROUTES_ERROR.format(userId))
        }
        userAdminRepository.deleteByUserId(userId)
        logger.info("User with id: $userId was successfully unlinked")
    }

    override fun generateCode(username: String, adminId: String): CodeGeneratedResponse {
        val userId = keycloakService.tryGetUserIdByUsername(username)
        require(userId != null) {
            throw ResourceNotFoundException(USER_NOT_FOUND_ERROR.format(username))
        }

        val generatedCode = linkTokenUtil.generateToken(userId, adminId, MINUTES_EXPIRATION)
        return CodeGeneratedResponse().code(generatedCode)
    }

    override fun getUsersByAdmin(adminId: String): List<GetUserResponse> {
        val userIds = userAdminRepository.findByAdminId(adminId).map { it.userId }
        return userIds.asSequence()
            .map { GetUserResponse()
                .id(it)
                .username(keycloakService.getUsernameByUserId(it))
                .adminUsername(keycloakService.getUsernameByUserId(adminId))
            }
            .toList()
    }

    override fun getUserDetails(username: String): GetUserDetailsResponse {
        val getUserDetailsResponse = GetUserDetailsResponse()
            .id(keycloakService.tryGetUserIdByUsername(username))
            .username(username)
//            .routes(routeClient.findRouteByUser())
        return getUserDetailsResponse
    }

    override fun checkUserBelongAdmin(userId: String, adminId: String): CheckAdminResponse {
        val isUserBelongAdmin = userAdminRepository.findByAdminIdAndUserId(adminId, userId) != null
        return CheckAdminResponse().isUserBelongAdmin(isUserBelongAdmin)
    }

    private fun tryGetAdminUsernameByUserId(userId: String): String? {
        return userAdminRepository.findByUserId(userId)?.adminId?.let {
            keycloakService.getUsernameByUserId(it)
        }
    }
}