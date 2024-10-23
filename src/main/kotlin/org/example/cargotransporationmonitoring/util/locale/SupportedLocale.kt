package org.example.cargotransporationmonitoring.util.locale

enum class SupportedLocale(val code: String) {
    EN("en"),
    RU("ru");

    companion object {
        fun fromCode(code: String): SupportedLocale? {
            return entries.find { it.code == code }
        }
    }
}