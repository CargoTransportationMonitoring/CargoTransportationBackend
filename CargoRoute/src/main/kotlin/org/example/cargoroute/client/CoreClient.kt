package org.example.cargoroute.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "CargoTransportationMonitoringApplication",
    url = "\${core.service.url}"
)
interface CoreClient {

    @GetMapping("api/v1/check-admin-membership")
    fun isUserBelongToAdmin(@RequestParam userId: String, @RequestParam adminId: String): Boolean
}
