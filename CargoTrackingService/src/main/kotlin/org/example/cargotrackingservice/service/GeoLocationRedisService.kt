package org.example.cargotrackingservice.service

import org.example.cargotrackingservice.dto.GeoLocation
import org.example.cargotrackingservice.entity.GeoLocationEntity
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service

@Service
class GeoLocationRedisService(
    private val redisTemplate: RedisTemplate<String, GeoLocationEntity>
) {

    companion object {
        private const val LOCATION_KEY_PREFIX = "location:"
    }

    private val valueOps: ValueOperations<String, GeoLocationEntity>
        get() = redisTemplate.opsForValue()

    fun saveLocation(email: String, geo: GeoLocationEntity) {
        valueOps["$LOCATION_KEY_PREFIX:$email"] = geo
    }

    fun getLocation(email: String): GeoLocation {
        return valueOps["$LOCATION_KEY_PREFIX:$email"]!!.toDto(email)
    }
}