package org.example.cargotransporationmonitoring.users.repository

import org.example.cargotransporationmonitoring.users.entity.UserAdmin
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAdminRepository : CrudRepository<UserAdmin, String> {
    fun findByAdminId(adminId: String): List<UserAdmin>

    @Modifying
    @Query("DELETE FROM user_admin WHERE user_id = :userId")
    fun deleteByUserId(userId: String): Int
}
