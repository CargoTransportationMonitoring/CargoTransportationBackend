package org.example.cargoroute

import org.example.cargocommon.security.keycloak.KeycloakService
import org.example.cargocommon.util.SecurityUtils
import org.example.cargoroute.client.CoreClient
import org.example.cargoroute.repository.CoordinateRepository
import org.example.cargoroute.repository.RouteRepository
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
abstract class AbstractTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @AfterEach
    fun clear() {
        coordinateRepository.deleteAll()
        routeRepository.deleteAll()
    }

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var routeRepository: RouteRepository

    @Autowired
    protected lateinit var coordinateRepository: CoordinateRepository

    @MockBean
    protected lateinit var keycloakService: KeycloakService

    @MockBean
    protected lateinit var securityUtils: SecurityUtils

    @MockBean
    protected lateinit var coreClient: CoreClient
}
