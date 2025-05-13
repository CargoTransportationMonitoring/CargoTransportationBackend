package org.example.cargotrackingservice

import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@AutoConfigureMockMvc
@TestConfiguration
@ActiveProfiles("test")
class AbstractTest {

	companion object {
		private val redisContainer = GenericContainer(DockerImageName.parse("redis:7.0.5"))
			.withExposedPorts(6379)

		@JvmStatic
		@BeforeAll
		fun startContainer() {
			redisContainer.start()
		}

		// Передаем настройки в Spring Boot
		@JvmStatic
		@DynamicPropertySource
		fun overrideRedisProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.data.redis.host") { redisContainer.host }
			registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379) }
		}
	}
}
