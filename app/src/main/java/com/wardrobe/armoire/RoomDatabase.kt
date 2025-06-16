package com.wardrobe.armoire

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wardrobe.armoire.model.cart.CartDao
import com.wardrobe.armoire.model.order.OrderDao
import com.wardrobe.armoire.model.outfit.OutfitDao
import com.wardrobe.armoire.model.outfit.OutfitModel
import com.wardrobe.armoire.model.shop.ShopDao
import com.wardrobe.armoire.model.shop.ShopModel
import com.wardrobe.armoire.model.user.UserDao
import com.wardrobe.armoire.model.user.UserModel
import com.wardrobe.armoire.model.wardrobe.WardrobeDao
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import com.wardrobe.armoire.model.cart.CartModel
import com.wardrobe.armoire.model.order.OrderModel
import com.wardrobe.armoire.utils.Converters

@Database(
    entities = [WardrobeModel::class, UserModel::class, OutfitModel::class, ShopModel::class, OrderModel::class, CartModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wardrobeDao(): WardrobeDao
    abstract fun userDao(): UserDao
    abstract fun outfitDao(): OutfitDao
    abstract fun shopDao() : ShopDao
    abstract fun orderDao() : OrderDao
    abstract fun cartDao() : CartDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "armoire_database"
                )
                    .createFromAsset("armoire_database.db")
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
