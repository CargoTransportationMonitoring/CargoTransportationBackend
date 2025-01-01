package org.example.cargotransporationmonitoring.advice

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerExceptionHandler : ResponseEntityExceptionHandler() {

//    @ExceptionHandler(Exception::class)
//    protected fun handleException(
//        request: HttpServletRequest?,
//        ex: Exception
//    ): ResponseEntity<ApiException> {
//        var status = HttpStatus.INTERNAL_SERVER_ERROR.value()
//        val message = ex.message
//
//        val responseStatus = AnnotationUtils.findAnnotation(
//            ex.javaClass,
//            ResponseStatus::class.java
//        )
//        if (responseStatus != null) {
//            status = responseStatus.value.value()
//        }
//        logger.error(message)
//
//        return ResponseEntity.status(status)
//            .contentType(MediaType.APPLICATION_JSON)
//            .body(ApiException(status, message))
//    }
//
//    @ExceptionHandler(AccessDeniedException::class)
//    fun handleAccessDeniedException(exception: AccessDeniedException): ResponseEntity<String> {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied")
//    }
}