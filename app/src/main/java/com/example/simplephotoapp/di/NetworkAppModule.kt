package com.example.simplephotoapp.di

import android.content.Context
import com.example.simplephotoapp.PhotoApplication
import com.example.simplephotoapp.data.PhotoRepository
import com.example.simplephotoapp.database.PhotoDatabase
import com.example.simplephotoapp.database.getDatabase
import com.example.simplephotoapp.network.UnsplashApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkAppModule {
    
    @Singleton
    @Provides
    fun provideApi(): UnsplashApiService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(UnsplashApiService.BASE_URL)
            .build()

        return retrofit.create(UnsplashApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(photoApplication: PhotoApplication): PhotoDatabase =
        getDatabase(photoApplication)


    @Singleton
    @Provides
    fun providePhotoApplication(@ApplicationContext app: Context): PhotoApplication =
        app as PhotoApplication
}