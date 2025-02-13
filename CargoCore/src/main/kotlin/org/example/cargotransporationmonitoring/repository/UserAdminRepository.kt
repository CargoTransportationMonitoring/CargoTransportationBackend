package org.example.cargotransporationmonitoring.repository

import org.example.cargotransporationmonitoring.entity.UserAdmin
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

    @Query("SELECT * FROM user_admin where admin_id = :adminId and user_id = :userId")
    fun findByAdminIdAndUserId(adminId: String, userId: String): UserAdmin?

    @Query("SELECT * from user_admin where user_id = :userId")
    fun findByUserId(userId: String): UserAdmin?
}
