package com.wardrobe.armoire

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wardrobe.armoire.model.user.UserDao
import com.wardrobe.armoire.model.user.UserModel
import com.wardrobe.armoire.model.wardrobe.WardrobeDao
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import com.wardrobe.armoire.utils.Converters

@Database(
    entities = [WardrobeModel::class, UserModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wardrobeDao(): WardrobeDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "armoire_database"
                            ).fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
