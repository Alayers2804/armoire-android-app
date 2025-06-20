package com.wardrobe.armoire.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
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

object ImageUtils {
    fun convertWardrobesToBase64(context: Context, wardrobes: List<WardrobeModel>): List<String> {
        return wardrobes.mapNotNull { wardrobeToBase64(context, it) }
    }

    fun downloadAndSaveImage(context: Context, imageUrl: String): File {
        val input = URL(imageUrl).openStream()
        val file = File(context.filesDir, "outfit_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { it.write(input.readBytes()) }
        return file
    }

    fun wardrobeToBase64(context: Context, wardrobe: WardrobeModel): String? {
        return try {
            val bytes: ByteArray = when {
                wardrobe.path.startsWith("/data") || wardrobe.path.startsWith(context.filesDir.absolutePath) ->
                    File(wardrobe.path).readBytes()

                wardrobe.path.endsWith(".jpg") || wardrobe.path.endsWith(".png") ->
                    context.assets.open(wardrobe.path).use { it.readBytes() }

                else -> {
                    val resId = context.resources.getIdentifier(wardrobe.path, "drawable", context.packageName)
                    if (resId != 0) {
                        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        stream.toByteArray()
                    } else throw FileNotFoundException("Resource not found: ${wardrobe.path}")
                }
            }

            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e("ImageUtils", "Failed to convert image: ${wardrobe.path}, ${e.message}")
            null
        }
    }
}


