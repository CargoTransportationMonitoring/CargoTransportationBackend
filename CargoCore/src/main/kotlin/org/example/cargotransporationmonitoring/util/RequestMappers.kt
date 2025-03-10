package org.example.cargotransporationmonitoring.util

import com.example.model.users.RegisterUserRequest
import com.example.model.users.UpdateUserRequest
import org.example.cargocommon.dto.CommonRegisterUserRequest
import org.example.cargocommon.dto.CommonUpdateUserRequest

fun toCommonRegisterUserRequest(registerUserRequest: RegisterUserRequest): CommonRegisterUserRequest {
    return CommonRegisterUserRequest(
        password = registerUserRequest.password,
        username = registerUserRequest.username,
        email = registerUserRequest.email
    )
}

fun toCommonUpdateUserRequest(updateUserRequest: UpdateUserRequest): CommonUpdateUserRequest {
    return CommonUpdateUserRequest(
        name = updateUserRequest.name,
        surname = updateUserRequest.surname
    )
}
