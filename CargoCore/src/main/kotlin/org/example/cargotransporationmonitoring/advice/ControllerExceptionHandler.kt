package org.example.cargotransporationmonitoring.advice

import com.example.model.users.ErrorResponse
import org.example.cargotransporationmonitoring.exception.AbstractCargoTransportationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.reflect.InvocationTargetException

@ControllerAdvice
class ControllerExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(AbstractCargoTransportationException::class)
    fun handleCustomException(ex: AbstractCargoTransportationException, model: Model): ResponseEntity<ErrorResponse> {
        val httpStatus = getHttpStatusFromException(ex)
        return createErrorResponse(ex.message, httpStatus)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException, model: Model): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }

    private fun getHttpStatusFromException(ex: Throwable): HttpStatus {
        return try {
            val responseStatusAnnotation = ex.javaClass.getAnnotation(ResponseStatus::class.java)
            responseStatusAnnotation?.value ?: HttpStatus.INTERNAL_SERVER_ERROR
        } catch (e: NoSuchMethodException) {
            HttpStatus.INTERNAL_SERVER_ERROR
        } catch (e: InvocationTargetException) {
            HttpStatus.INTERNAL_SERVER_ERROR
        }
    }

    private fun createErrorResponse(message: String?, status: HttpStatus): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse()
        errorResponse.error(message)
        return ResponseEntity(errorResponse, status)
    }
}