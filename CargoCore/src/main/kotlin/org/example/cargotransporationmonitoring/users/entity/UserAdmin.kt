package org.example.cargotransporationmonitoring.users.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("user_admin")
data class UserAdmin(
    @Id
    val id: Long? = null, // ID может быть null, так как оно задаётся базой или вручную
    val userId: String,
    val adminId: String
) {
    class Builder {
        private var id: Long? = null
        private var userId: String = ""
        private var adminId: String = ""

        fun id(id: Long?) = apply { this.id = id }
        fun userId(userId: String) = apply { this.userId = userId }
        fun adminId(adminId: String) = apply { this.adminId = adminId }

        fun build(): UserAdmin {
            return UserAdmin(id, userId, adminId)
        }
    }
}
