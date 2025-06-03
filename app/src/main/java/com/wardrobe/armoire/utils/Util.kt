package com.wardrobe.armoire.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.security.MessageDigest
import java.util.Date
import javax.crypto.SecretKey


private const val JWT_KEY = "3213131_secret_key_that_is_long_enough_123456"

private fun getSignInKey(): SecretKey {
    val keyBytes: ByteArray = JWT_KEY.toByteArray()
    return Keys.hmacShaKeyFor(keyBytes)
}

object HashUtil {

    private const val SALT = "423123"
    private val digest = MessageDigest.getInstance("SHA-512")

    fun hash(vararg inputs: String): String {
        val combined = inputs.joinToString(separator = "|") + SALT
        val bytes = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

object GenerateTokenUtil {

    fun generateToken(username: String, timeStamp: Date): String {

        val expireInterval = 2000000
        return Jwts.builder().subject(username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expireInterval))
            .signWith(getSignInKey())
            .compact()
    }


}

object DecodeToken {

    fun decodeToken(token: String): DecodedResult? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .payload

            val username = claims.subject
            val isExpired = claims.expiration.before(Date())

            DecodedResult(username, isExpired)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    data class DecodedResult(
        val username: String?,
        val isExpired: Boolean
    )
}
