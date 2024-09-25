package com.schoolkiller.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.schoolkiller.data.daos.PictureDao
import com.schoolkiller.data.entities.Picture
import com.schoolkiller.utils.Constants

@Database(entities = [Picture::class], version = 1)
abstract class SchoolKillerDatabase : RoomDatabase() {

    abstract fun pictureDao(): PictureDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolKillerDatabase? = null

        fun getDatabase(context: Context): SchoolKillerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolKillerDatabase::class.java,
                    Constants.DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}