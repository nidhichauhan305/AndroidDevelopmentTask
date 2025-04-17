package com.jetpack.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jetpack.ui.manga.MangaResponse

@Database(entities = [(UserLoginInfoClass::class),(MangaResponse.Data::class)], version = 2)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

}