package org.example.cargotrackingservice.controller

import org.example.cargotrackingservice.service.GeoLocationRedisService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

@RestController
@RequestMapping("/sse")
class SseController(
    private val geoLocationRedisService: GeoLocationRedisService
) {

    @GetMapping("/stream")
    fun stream(@RequestParam email: String): SseEmitter {
        val emitter = SseEmitter(0L)
        val stopFlag = AtomicBoolean(false)

        val worker = thread(start = false) {
            try {
                while (!stopFlag.get()) {
                    val location = runCatching {
                        geoLocationRedisService.getLocation(email)
                    }.getOrNull()

                    if (location != null) {
                        try {
                            emitter.send(location)
                        } catch (ex: IOException) {
                            // Клиент отключился
                            break
                        } catch (ex: IllegalStateException) {
                            // emitter завершён, но ещё не удалён
                            break
                        }
                    }

                    Thread.sleep(5000)
                }
            } catch (ex: Exception) {
                Thread.currentThread().interrupt()
            }
        }

        val cleanup = {
            stopFlag.set(true)
            worker.interrupt()
        }

        emitter.onCompletion(cleanup)
        emitter.onTimeout(cleanup)
        emitter.onError { cleanup() }

        worker.start()
        return emitter
    }
}