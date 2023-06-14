package com.hamzafrd.storia.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hamzafrd.storia.data.repository.AuthRepository
import com.hamzafrd.storia.di.Injection
import com.hamzafrd.storia.ui.viewModel.AuthViewModel

class AuthViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val pref: SessionPreferences
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository, pref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null
        fun getInstance(pref: SessionPreferences): AuthViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AuthViewModelFactory(Injection.provideAuthRepository(pref), pref)
            }.also { instance = it }
    }
}