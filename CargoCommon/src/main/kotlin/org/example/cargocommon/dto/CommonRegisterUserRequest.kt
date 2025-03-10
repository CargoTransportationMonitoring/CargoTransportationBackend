package org.example.cargocommon.dto

data class CommonRegisterUserRequest(
    val password: String,
    val username: String,
    val email: String
)
