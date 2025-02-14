package org.example.cargoroute.util

import org.example.cargoroute.util.ErrorMessages.JWT_SUBJECT_NOT_FOUND
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object SecurityUtil {

    private const val SUB_CLAIM = "sub"

    fun getCurrentUserId(): String {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        return jwt.claims[SUB_CLAIM] as? String ?: throw IllegalArgumentException(JWT_SUBJECT_NOT_FOUND)
    }
}