package com.hamzafrd.storia.di

import android.content.Context
import com.hamzafrd.storia.data.local.room.StoryDatabase
import com.hamzafrd.storia.data.repository.StoryRepository
import com.hamzafrd.storia.data.remote.retrofit.ApiConfig
import com.hamzafrd.storia.data.repository.AuthRepository
import com.hamzafrd.storia.helper.SessionPreferences

object Injection {
    fun provideRepository(context: Context, pref:SessionPreferences): StoryRepository {
        val apiService = ApiConfig.getApiService(pref)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService,database)
    }

    fun provideAuthRepository(pref: SessionPreferences): AuthRepository {
        val apiService = ApiConfig.getApiService(pref)
        return AuthRepository.getInstance(apiService)
    }
}