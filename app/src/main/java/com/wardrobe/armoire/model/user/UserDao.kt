package com.wardrobe.armoire.model.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: UserModel)

    @Query("SELECT * FROM user_master_data WHERE username = :username AND password = :password LIMIT 1")
    suspend fun authenticateUser(username: String, password: String): UserModel?

    @Query("SELECT * FROM user_master_data WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserModel?

    @Query("SELECT * FROM user_master_data WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserModel?

    @Delete
    suspend fun deleteUser(user: UserModel)
}