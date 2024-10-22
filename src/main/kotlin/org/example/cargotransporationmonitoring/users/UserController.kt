package org.example.cargotransporationmonitoring.users

import com.example.api.users.ApiApi
import com.example.model.users.GetUserResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController: ApiApi {

    override fun apiV1UsersGet(): ResponseEntity<List<GetUserResponse>> {
        return ResponseEntity.ok(listOf(GetUserResponse()))
    }
}