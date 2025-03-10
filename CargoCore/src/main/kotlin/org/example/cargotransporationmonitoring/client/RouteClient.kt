package org.example.cargotransporationmonitoring.client

import com.example.model.users.RouteExistResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "RouteService",
    url = "\${route.service.url}"
)
interface RouteClient {

    @GetMapping("/api/v1/user/{userId}/routes-exist")
    fun isExistByUserId(@PathVariable("userId") userId: String): RouteExistResponse
}