package com.schoolkiller.di

import android.content.Context
import com.schoolkiller.SchoolKillerApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: SchoolKillerApplication) : Context = application.applicationContext

}