package org.example.cargotransporationmonitoring.util.locale.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.example.cargotransporationmonitoring.messages.LocaleMessages.LOCALE_VALIDATION_ERROR
import org.example.cargotransporationmonitoring.util.exception.LocaleValidationException
import org.example.cargotransporationmonitoring.util.locale.SupportedLocale

class LangValidator : ConstraintValidator<ValidLang, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
        if (SupportedLocale.fromCode(value) == null) {
            throw LocaleValidationException(LOCALE_VALIDATION_ERROR, value)
        }
        return true
    }
}
