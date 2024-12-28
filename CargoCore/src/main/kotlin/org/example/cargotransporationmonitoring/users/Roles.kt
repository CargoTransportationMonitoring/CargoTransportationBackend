package org.example.cargotransporationmonitoring.users

enum class Roles(private val role: String) {
    ADMIN("admin"),
    USER("user");

    fun getRoleName(): String {
        return role
    }
}