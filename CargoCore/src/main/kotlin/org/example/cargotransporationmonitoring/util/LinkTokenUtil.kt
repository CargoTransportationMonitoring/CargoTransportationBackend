package org.example.cargotransporationmonitoring.util

import org.example.cargotransporationmonitoring.entity.UserAdmin
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.*

@Component
class LinkTokenUtil {

    @Value("\${secret.key}")
    private lateinit var secretKey: String

    companion object {
        private const val ALGORITHM_AES = "AES"
        private const val TOKEN_EXPIRED = "Token expired"
        private const val TOKEN_INVALID = "Token invalid"
    }

    fun generateToken(
        driverId: String,
        adminId: String,
        validMinutes: Long
    ): String {
        val expirationTime = System.currentTimeMillis() + validMinutes * 60 * 1000
        val nonce = UUID.randomUUID().toString()
        val payload = "$driverId|$adminId|$expirationTime|$nonce"

        val cipher = Cipher.getInstance(ALGORITHM_AES)
        val keySpec = SecretKeySpec(secretKey.toByteArray(), ALGORITHM_AES)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedData = cipher.doFinal(payload.toByteArray())

        return Base64.getEncoder().encodeToString(encryptedData)
    }

    fun decryptToken(token: String): UserAdmin {
        val cipher = Cipher.getInstance(ALGORITHM_AES)
        val keySpec = SecretKeySpec(secretKey.toByteArray(), ALGORITHM_AES)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decryptedData = cipher.doFinal(Base64.getDecoder().decode(token))
        val payload = String(decryptedData)

        val parts = payload.split("|")
        require(parts.size == 4) { TOKEN_INVALID }

        val driverId = parts[0]
        val adminId = parts[1]
        val expirationTime = parts[2].toLong()

        // Проверка срока действия токена
        require(System.currentTimeMillis() <= expirationTime) { TOKEN_EXPIRED }

        return UserAdmin.Builder()
            .userId(driverId)
            .adminId(adminId)
            .build()
    }
}
