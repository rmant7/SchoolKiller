package com.schoolkiller.di

import android.content.Context
import androidx.room.Room
import com.schoolkiller.data_Layer.daos.PictureDao
import com.schoolkiller.data_Layer.database.SchoolKillerDatabase
import com.schoolkiller.data_Layer.repositories.PictureRepository
import com.schoolkiller.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSchoolKillerDatabase(@ApplicationContext context: Context): SchoolKillerDatabase {
        return Room.databaseBuilder(context, SchoolKillerDatabase::class.java, Constants.DATABASE_NAME)
            .build()
    }


    @Provides
    @Singleton
    fun providePictureDao(database: SchoolKillerDatabase): PictureDao {
        return database.pictureDao()
    }

    @Provides
    @Singleton
    fun providePictureRepository(pictureDao: PictureDao): PictureRepository {
        return PictureRepository(pictureDao)
    }

}