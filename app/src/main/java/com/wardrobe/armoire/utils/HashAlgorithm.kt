package com.wardrobe.armoire.utils

import java.security.MessageDigest

object HashUtil {

    private const val SALT = "423123"
    private val digest = MessageDigest.getInstance("SHA-512")

    fun hash(vararg inputs: String): String {
        val combined = inputs.joinToString(separator = "|") + SALT
        val bytes = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}