package org.example.cargotransporationmonitoring.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping("/login")
    fun loginTest(): ResponseEntity<String> {
        return ResponseEntity.ok("login test")
    }

    @GetMapping("/internal")
    fun internalTest(): ResponseEntity<String> {
        return ResponseEntity.ok("internal test")
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin')")
    fun adminTest(): ResponseEntity<String> {
        return ResponseEntity.ok("admin test")
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('user')")
    fun userTest(): ResponseEntity<String> {
        return ResponseEntity.ok("user test")
    }
}