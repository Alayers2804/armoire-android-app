package com.wardrobe.armoire.ui.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.model.user.Gender
import com.wardrobe.armoire.model.user.UserDao
import com.wardrobe.armoire.model.user.UserModel
import com.wardrobe.armoire.utils.HashUtil

class AuthenticationViewmodel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

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

        return loginResult != null
    }
}