package org.example.cargotransporationmonitoring.util.locale.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [LangValidator::class])
annotation class ValidLang(
    val message: String = "Invalid language",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
