package com.wardrobe.armoire.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Settings")

const val preferenceDefaultValue = "Not Set"

class Preferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val token = stringPreferencesKey(UserPreferences.User_Token.name)
    private val name = stringPreferencesKey(UserPreferences.Username.name)
    private val email = stringPreferencesKey(UserPreferences.Email.name)

    fun getUserToken(): Flow<String> =
        dataStore.data.map { it[token] ?: preferenceDefaultValue }


    fun getUserName(): Flow<String> =
        dataStore.data.map { it[name] ?: preferenceDefaultValue }

    fun getEmail(): Flow<String> =
        dataStore.data.map { it[email] ?: preferenceDefaultValue }

    suspend fun saveLoginSession(
        usertoken: String,
        username: String,
        email: String,
    ) {
        dataStore.edit { preferences ->
            preferences[token] = usertoken
            preferences[name] = username
            preferences[this.email] = email
        }
    }

    suspend fun clearLoginSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: com.wardrobe.armoire.utils.Preferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): com.wardrobe.armoire.utils.Preferences {
            return INSTANCE ?: synchronized(this) {
                val instance = Preferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}

enum class UserPreferences {
    Username, User_Token, Email
}

