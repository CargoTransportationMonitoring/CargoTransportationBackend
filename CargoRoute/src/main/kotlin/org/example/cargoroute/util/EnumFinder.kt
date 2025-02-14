package org.example.cargoroute.util

object EnumFinder {
    fun <T : Enum<T>> findByName(enumClass: Class<T>, name: String?): T? {
        return enumClass.enumConstants?.find { it.name.equals(name, ignoreCase = true) }
    }
}