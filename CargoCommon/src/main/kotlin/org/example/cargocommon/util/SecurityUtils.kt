package org.example.cargocommon.util

import ErrorMessages.JWT_TOKEN_NOT_FOUND
import ErrorMessages.SUBJECT_NOT_FOUND_IN_JWT
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object SecurityUtils {

    private const val SUB_CLAIM = "sub"

    fun getCurrentUserId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwt = authentication.principal as? Jwt
            ?: throw IllegalArgumentException(JWT_TOKEN_NOT_FOUND)

        return jwt.claims[SUB_CLAIM] as? String
            ?: throw IllegalArgumentException(SUBJECT_NOT_FOUND_IN_JWT)
    }
}
