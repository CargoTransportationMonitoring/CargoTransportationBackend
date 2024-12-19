package org.example.cargotransporationmonitoring.util.locale

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ReloadableResourceBundleMessageSource

object MessageUtil {

    private val messageSource: MessageSource = ReloadableResourceBundleMessageSource().apply {
        setBasename("classpath:locale/messages")
        setDefaultEncoding("UTF-8")
    }

    fun getLocalizedMessage(messageKey: String, vararg args: String): String {
        val locale = LocaleContextHolder.getLocale()
        return messageSource.getMessage(messageKey, args, locale)
    }
}
