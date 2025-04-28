package org.example.cargotrackingservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.cargotrackingservice.entity.GeoLocationEntity
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun geoLocationRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, GeoLocationEntity> {
        val template = RedisTemplate<String, GeoLocationEntity>()
        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .findAndRegisterModules()
        val serializer = Jackson2JsonRedisSerializer(GeoLocationEntity::class.java)
        serializer.setObjectMapper(objectMapper)

        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        return template
    }
}
