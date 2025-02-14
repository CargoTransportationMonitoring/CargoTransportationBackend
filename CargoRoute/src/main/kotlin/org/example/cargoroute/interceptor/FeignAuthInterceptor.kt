package org.example.cargoroute.interceptor

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class FeignAuthInterceptor : RequestInterceptor {

    override fun apply(requestTemplate: RequestTemplate?) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.credentials is Jwt) {
            val token = (authentication.credentials as Jwt).tokenValue
            requestTemplate?.header("Authorization", "Bearer $token")
        }
    }
}