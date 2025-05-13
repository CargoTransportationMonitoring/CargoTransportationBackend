package org.example.cargotransporationmonitoring.controller

import com.example.model.users.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.cargocommon.dto.CommonRegisterUserRequest
import org.example.cargocommon.dto.CommonRoles
import org.example.cargotransporationmonitoring.AbstractTest
import org.example.cargotransporationmonitoring.entity.UserAdmin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.UserRepresentation
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

class UserControllerTest : AbstractTest() {

    companion object {
        private const val USER_ID = "1"
        private const val USERNAME = "iwaa_user"
        private const val EMAIL = "iwaa0303@gmail.com"
        private const val PASSWORD = "12345678"

        private const val ADMIN_ID = "2"
        private const val ADMIN_USERNAME = "iwaa_admin"
    }

    @Test
    fun testApiV1UserPost() {
        whenever(keycloakService.createKeyCloakUser(CommonRegisterUserRequest(PASSWORD, USERNAME, EMAIL)))
            .thenReturn(USER_ID)
        doNothing().`when`(keycloakService).addRolesToUser(USER_ID, listOf(CommonRoles.USER))

        val request = RegisterUserRequest()
            .username(USERNAME)
            .password(PASSWORD)
            .email(EMAIL)

        val result = mockMvc.post("/api/v1/user") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }.andReturn()

        val getUserResponse: GetUserResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, GetUserResponse::class.java)
        Assertions.assertEquals(USER_ID, getUserResponse.id)
        Assertions.assertEquals(USERNAME, getUserResponse.username)
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1UserIdGet() {
        userAdminRepository.save(UserAdmin.Builder()
            .userId(USER_ID)
            .adminId(ADMIN_ID)
            .build()
        )
        whenever(keycloakService.findKeyCloakUserById(USER_ID))
            .thenReturn(UserRepresentation().apply {
                id = USER_ID
                username = USERNAME
                email = EMAIL
            })

        whenever(securityUtils.getCurrentUserId()).thenReturn(ADMIN_ID)
        whenever(keycloakService.getUsernameByUserId(ADMIN_ID)).thenReturn(ADMIN_USERNAME)

        val result = mockMvc.get("/api/v1/user/$USER_ID").andExpect {
            status { isOk() }
        }.andReturn()

        val getUserResponse: GetUserResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, GetUserResponse::class.java)
        Assertions.assertEquals(USER_ID, getUserResponse.id)
        Assertions.assertEquals(USERNAME, getUserResponse.username)
        Assertions.assertEquals(ADMIN_USERNAME, getUserResponse.adminUsername)
    }

    @Test
    @WithMockUser(username = USER_ID)
    fun testApiV1UserUserIdPut() {

        whenever(routeClient.isExistByUserId(USER_ID))
            .thenReturn(RouteExistResponse().isUserExist(false))
        doNothing().`when`(keycloakService).deleteKeyCloakUserById(USER_ID)

        userAdminRepository.save(UserAdmin.Builder()
            .userId(USER_ID)
            .adminId(ADMIN_ID)
            .build()
        )
        Assertions.assertEquals(1, userAdminRepository.count())
        mockMvc.delete("/api/v1/user/${USER_ID}").andExpect {
            status { isNoContent() }
        }.andReturn()
        Assertions.assertEquals(0, userAdminRepository.count())
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1CheckAdminMembershipGer() {
        userAdminRepository.save(UserAdmin.Builder()
            .userId(USER_ID)
            .adminId(ADMIN_ID)
            .build()
        )

        val result = mockMvc.get("/api/v1/check-admin-membership?userId=$USER_ID&adminId=$ADMIN_ID").andExpect {
            status { isOk() }
        }.andReturn()

        val checkAdminResponse: CheckAdminResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, CheckAdminResponse::class.java)
        Assertions.assertEquals(true, checkAdminResponse.isUserBelongAdmin)
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1UserGenerateCodeGet() {
        whenever(securityUtils.getCurrentUserId()).thenReturn(ADMIN_ID)

        whenever(keycloakService.tryGetUserIdByUsername(USERNAME))
            .thenReturn(USER_ID)
        val result = mockMvc.get("/api/v1/user/generate-code?username=$USERNAME").andExpect {
            status { isOk() }
        }.andReturn()

        val codeGeneratedResponse: CodeGeneratedResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, CodeGeneratedResponse::class.java)
        val userAdmin = linkTokenUtil.decryptToken(codeGeneratedResponse.code)
        Assertions.assertEquals(USER_ID, userAdmin.userId)
        Assertions.assertEquals(ADMIN_ID, userAdmin.adminId)
    }

    @Test
    @WithMockUser(username = USER_ID)
    fun testApiV1UserUserIdLinkPut() {
        val code = linkTokenUtil.generateToken(USER_ID, ADMIN_ID, 10)
        mockMvc.put("/api/v1/user/$USER_ID/link") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(LinkUserRequest().code(code))
        }.andExpect {
            status { isOk() }
        }
        userAdminRepository.findByUserId(USER_ID)?.let { Assertions.assertEquals(ADMIN_ID, it.adminId) }
            ?: Assertions.fail("UserAdmin not found")
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1UsersGet() {
        whenever(securityUtils.getCurrentUserId()).thenReturn(ADMIN_ID)
        whenever(keycloakService.getUsernameByUserId(USER_ID)).thenReturn(USERNAME)
        whenever(keycloakService.getUsernameByUserId(ADMIN_ID)).thenReturn(ADMIN_USERNAME)

        userAdminRepository.save(UserAdmin.Builder()
            .userId(USER_ID)
            .adminId(ADMIN_ID)
            .build()
        )
        val result = mockMvc.get("/api/v1/users").andExpect {
            status { isOk() }
        }.andReturn()

        val getUsersResponse: List<GetUserResponse> =
            jacksonObjectMapper().readValue(result.response.contentAsString, object : TypeReference<List<GetUserResponse>>() {})
        Assertions.assertEquals(1, getUsersResponse.size)
        Assertions.assertEquals(USER_ID, getUsersResponse[0].id)
        Assertions.assertEquals(ADMIN_USERNAME, getUsersResponse[0].adminUsername)
        Assertions.assertEquals(USERNAME, getUsersResponse[0].username)
    }
}