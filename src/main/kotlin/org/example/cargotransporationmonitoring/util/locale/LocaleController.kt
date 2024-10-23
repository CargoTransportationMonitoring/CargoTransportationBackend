package org.example.cargotransporationmonitoring.util.locale

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.cargotransporationmonitoring.messages.LocaleMessages.LOCALE_VALIDATION_ERROR
import org.example.cargotransporationmonitoring.messages.LocaleMessages.LOCALE_CHANGED
import org.example.cargotransporationmonitoring.util.exception.LocaleValidationException
import org.example.cargotransporationmonitoring.util.locale.validation.ValidLang
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.LocaleResolver
import java.util.*

@RestController
@RequestMapping("/api/v1")
class LocaleController(private val localeResolver: LocaleResolver) {

    @GetMapping("/setLocale")
    fun setLocale(
        @RequestParam @ValidLang lang: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        val locale = Locale.forLanguageTag(lang)
        localeResolver.setLocale(request, response, locale)
        return ResponseEntity.ok(MessageUtil.getLocalizedMessage(LOCALE_CHANGED, lang))
    }
}
