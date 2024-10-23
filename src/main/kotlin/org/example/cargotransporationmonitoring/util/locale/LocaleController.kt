package org.example.cargotransporationmonitoring.util.locale

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.cargotransporationmonitoring.messages.LocaleMessages.ERROR_LOCALE_CHANGED
import org.example.cargotransporationmonitoring.messages.LocaleMessages.LOCALE_CHANGED
import org.example.cargotransporationmonitoring.util.exception.LocaleChangedException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.LocaleResolver
import java.util.*

@RestController
@RequestMapping("/api/v1")
class LocaleController(private val localeResolver: LocaleResolver) {

    @GetMapping("/setLocale")
    fun setLocale(
        @RequestParam lang: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        if (lang !in VALID_LOCALE) throw LocaleChangedException(ERROR_LOCALE_CHANGED, lang)
        val locale = Locale.forLanguageTag(lang)
        localeResolver.setLocale(request, response, locale)
        return ResponseEntity.ok(MessageUtil.getLocalizedMessage(LOCALE_CHANGED, lang))
    }

    companion object {
        val VALID_LOCALE = listOf("en", "ru")
    }
}
