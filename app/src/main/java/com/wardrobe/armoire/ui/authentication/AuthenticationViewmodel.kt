package com.wardrobe.armoire.ui.authentication

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.model.user.Gender
import com.wardrobe.armoire.model.user.UserDao
import com.wardrobe.armoire.model.user.UserModel
import com.wardrobe.armoire.utils.GenerateTokenUtil
import com.wardrobe.armoire.utils.HashUtil
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.UserPreferences
import com.wardrobe.armoire.utils.preferenceDefaultValue
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date

class AuthenticationViewmodel(application: Application, private val pref: Preferences) :
    AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val currentTime = Date(System.currentTimeMillis())

    suspend fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        gender: Gender,
        style: List<String>?
    ) {

        val hashedPassword = HashUtil.hash(password)

        val userData = UserModel(
            name = name,
            username = username,
            password = hashedPassword,
            email = email,
            gender = gender.toString(),
            style = style
        )

        userDao.registerUser(userData)
    }

    suspend fun login(username: String, password: String): Boolean {
        val hashedPassword = HashUtil.hash(password)
        val loginResult = userDao.authenticateUser(username, hashedPassword)
        val email = loginResult?.email
        val uid = loginResult?.uid
        if (loginResult != null) {
            val token = GenerateTokenUtil.generateToken(username, currentTime)
            setUserPreferences(uid.toString(), token, username, email.toString())
        }
        return loginResult != null
    }

    fun getUserPreference(property: UserPreferences): LiveData<String> {
        return when (property) {
            UserPreferences.Uid -> pref.getUserUid().asLiveData()
            UserPreferences.User_Token -> pref.getUserToken().asLiveData()
            UserPreferences.Username -> pref.getUserName().asLiveData()
            UserPreferences.Email -> pref.getEmail().asLiveData()
        }
    }

    fun setUserPreferences(
        uid : String,
        userToken: String,
        userName: String,
        email: String
        ) {
        viewModelScope.launch {
            pref.saveLoginSession(uid, userToken, userName, email)
        }
    }

    fun clearUserPreferences() {
        viewModelScope.launch {
            pref.clearLoginSession()
        }
    }

    fun getUserByUid(): LiveData<UserModel?> {
        val result = MutableLiveData<UserModel?>()
        viewModelScope.launch {
            pref.getUserUid().collect { uid ->
                if (uid != preferenceDefaultValue) {
                    val user = userDao.getUserById(uid)
                    result.postValue(user)
                }
            }
        }
        return result
    }
}