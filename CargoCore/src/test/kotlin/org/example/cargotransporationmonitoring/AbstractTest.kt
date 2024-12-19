package org.example.cargotransporationmonitoring

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AbstractTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    companion object {
        @ServiceConnection
        internal var postgres = PostgreSQLContainer(
            DockerImageName.parse("binakot/postgresql-postgis-timescaledb:latest")
                .asCompatibleSubstituteFor("postgres")
        )

        @BeforeAll
        @JvmStatic
        fun setup() {
            postgres.start()
        }
    }
}
