package org.example.cargotransporationmonitoring.users

import com.example.api.users.UserApi
import com.example.model.users.GetUserResponse
import com.example.model.users.RegisterUserRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController: UserApi {
    override fun apiV1UserRegisterPost(registerUserRequest: RegisterUserRequest?): ResponseEntity<GetUserResponse> {
        TODO("Not yet implemented")
    }

    override fun apiV1UserUserIdGet(userId: Int?): ResponseEntity<GetUserResponse> {
        TODO("Not yet implemented")
    }

    override fun apiV1UsersGet(): ResponseEntity<List<GetUserResponse>> {
        return ResponseEntity.ok(listOf(GetUserResponse()))
    }
}