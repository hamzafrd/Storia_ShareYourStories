package com.hamzafrd.storia.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hamzafrd.storia.data.repository.AuthRepository
import com.hamzafrd.storia.helper.SessionPreferences
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val pref: SessionPreferences
) : ViewModel() {

    fun getTokenSetting(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveSessionTokenSettings(token: String) {
        viewModelScope.launch {
            pref.saveTokenSetting(token)
            pref.saveSessionSetting(token.isNotEmpty())
        }
    }
    fun deleteSettings() {
        viewModelScope.launch {
            pref.deleteSession()
        }
    }

    fun login(email: String, password: String) = authRepository.getLoginResult(email, password)
    fun register(username: String, email: String, password: String) =
        authRepository.getRegisterResult(username, email, password)

    fun getToastText() = authRepository.toastText
}