package org.example.cargoroute.controller

import com.example.model.route.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.cargoroute.AbstractTest
import org.example.cargoroute.entity.CoordinateEntity
import org.example.cargoroute.entity.RouteEntity
import org.example.cargoroute.entity.RouteStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

class RouteControllerTest : AbstractTest() {

    companion object {
        private const val ROUTE_NAME = "Route1"
        private const val ROUTE_DESCRIPTION = "Route1 description"
        private val COORDINATE_ARRAY = listOf(
            CoordinateItem().latitude(1.3F).longitude(2.4F).isVisited(false),
            CoordinateItem().latitude(1.5F).longitude(2.5F).isVisited(false)
        )
        private const val CARGO_CARRIER_ID = "1"
        private const val CARGO_CARRIER_USERNAME = "iwaa_cargo_carrier"

        private const val ADMIN_ID = "2"
        private const val ADMIN_USERNAME = "iwaa_admin"
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1RoutePost() {
        whenever(coreClient.isUserBelongToAdmin(CARGO_CARRIER_ID, ADMIN_ID))
            .thenReturn(CheckAdminResponse().isUserBelongAdmin(true))
        whenever(securityUtils.getCurrentUserId()).thenReturn(ADMIN_ID)
        whenever(keycloakService.getUserIdByUsername(CARGO_CARRIER_USERNAME)).thenReturn(CARGO_CARRIER_ID)
        whenever(keycloakService.getUsernameByUserId(ADMIN_ID)).thenReturn(ADMIN_USERNAME)

        val request = CreateRouteRequest()
            .name(ROUTE_NAME)
            .description(ROUTE_DESCRIPTION)
            .assignedUsername(CARGO_CARRIER_USERNAME)
            .coordinates(COORDINATE_ARRAY)

        val result = mockMvc.post("/api/v1/route") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }.andReturn()
        val getRouteResponse: GetRouteResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, GetRouteResponse::class.java)

        val routeEntity = routeRepository.findById(getRouteResponse.id).get()
        Assertions.assertEquals(RouteStatus.NEW.name, routeEntity.routeStatus)
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1RouteGet() {
        val routeId = routeRepository.save(buildTestRouteEntity(RouteStatus.NEW)).id
        coordinateRepository.saveAll(buildTestCoordinatesList(routeId!!))
        val result = mockMvc.get("/api/v1/route/${routeId}")
            .andExpect {
                status { isOk() }
            }.andReturn()
        val getRouteResponse: GetRouteResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, GetRouteResponse::class.java)
        Assertions.assertEquals(RouteStatus.NEW.name, getRouteResponse.routeStatus)
        Assertions.assertEquals(routeId, getRouteResponse.id)
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1RouteGetNotFound() {
        mockMvc.get("/api/v1/route/12")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1RouteDelete() {
        val routeId = routeRepository.save(buildTestRouteEntity(RouteStatus.NEW)).id
        coordinateRepository.saveAll(buildTestCoordinatesList(routeId!!))
        val beforeDeleteRouteCounter = routeRepository.count()
        val beforeDeleteCoordinateCounter = coordinateRepository.count()
        mockMvc.delete("/api/v1/route/1")
            .andExpect {
                status { isNoContent() }
            }
        val afterDeleteRouteCounter = routeRepository.count()
        val afterDeleteCoordinateCounter = coordinateRepository.count()
        Assertions.assertEquals(1L, beforeDeleteRouteCounter - afterDeleteRouteCounter)
        Assertions.assertEquals(1L, beforeDeleteCoordinateCounter - afterDeleteCoordinateCounter)
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1RouteExistByUser() {
        whenever(keycloakService.getUsernameByUserId("1")).thenReturn("iwaa")
        val result = mockMvc.get("/api/v1/user/1/routes-exist")
            .andExpect {
                status { isOk() }
            }.andReturn()
        val routeExistResponse: RouteExistResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, RouteExistResponse::class.java)
        Assertions.assertEquals(false, routeExistResponse.isUserExist)
    }

    @Test
    @WithMockUser(roles = ["cargo"])
    fun testApiV1RouteIdMarkPoints() {
        val routeId = routeRepository.save(buildTestRouteEntity(RouteStatus.IN_PROGRESS)).id
        val coordinates = coordinateRepository.saveAll(buildTestCoordinatesList(routeId!!))

        val markPointsRequest = MarkPointsRequest()
            .coordinates(
                listOf(
                    CoordinateItem()
                        .id(coordinates.first().id)
                        .isVisited(true)
                )
            )
            .routeStatus(RouteStatus.IN_PROGRESS.name)

        val result = mockMvc.put("/api/v1/route/${routeId}/mark-points") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(markPointsRequest)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val getRouteResponse: GetRouteResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, GetRouteResponse::class.java)
        Assertions.assertEquals(true, getRouteResponse.coordinates.first().isVisited)
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun testApiV1RoutesGetWithFilter() {
        val routeName = "SPB -> Pushkin"
        val routeId = routeRepository.save(buildTestRouteEntity(RouteStatus.IN_PROGRESS, routeName)).id
        val coordinates = coordinateRepository.saveAll(buildTestCoordinatesList(routeId!!))

        val result = mockMvc.get("/api/v1/routes?routeName=SPB&routeStatus=IN_PROGRESS")
            .andExpect {
                status { isOk() }
            }.andReturn()

        val paginationResponse: PaginationResponse =
            jacksonObjectMapper().readValue(result.response.contentAsString, PaginationResponse::class.java)

        Assertions.assertEquals(routeName, paginationResponse.content.first().name)
    }

    private fun buildTestRouteEntity(routeStatusParameter: RouteStatus, nameParam: String? = null): RouteEntity {
        return RouteEntity.Builder()
            .apply {
                routeStatus = routeStatusParameter.name
                name = nameParam ?: "SPB -> Pushkin"
                description = "Доставка мебели"
                assignedUsername = "nikita"
                adminUsername = "iwaa"
            }
            .build()
    }

    private fun buildTestCoordinatesList(routeIdParam: Long): List<CoordinateEntity> {
        return listOf(
            CoordinateEntity.Builder()
                .apply {
                    latitude = 2.0
                    longitude = 1.0
                    routeId = routeIdParam
                    isVisited = false
                    order = 1
                }
                .build()
        )
    }
}