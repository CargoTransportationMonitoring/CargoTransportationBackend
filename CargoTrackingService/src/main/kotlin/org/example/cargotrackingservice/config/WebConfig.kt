package org.example.cargotrackingservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/sse/stream")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("*")
            .allowCredentials(true)
    }
}
