package com.hamzafrd.storia.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.hamzafrd.storia.data.Result
import com.hamzafrd.storia.data.remote.response.LoginResponse
import com.hamzafrd.storia.data.remote.response.MainResponse
import com.hamzafrd.storia.data.remote.retrofit.ApiService
import com.hamzafrd.storia.utils.Event
import com.hamzafrd.storia.utils.getErrorBodyMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository private constructor(
    private val apiService: ApiService
) {
    private val tokenResult = MediatorLiveData<Result<String>>()
    private val isRegistered = MediatorLiveData<Result<String>>()

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun getLoginResult(email: String, password: String): LiveData<Result<String>> {
        tokenResult.value = Result.Loading

        val login = apiService.login(email, password)
        login.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        Log.d(TAG, response.body().toString())
                        val token = responseBody.loginResult.token
                        tokenResult.value = Result.Success(token)
                        _toastText.value = Event(responseBody.message)
                    } else {
                        val result = Result.Error(getErrorBodyMessage(response))
                        tokenResult.value = result
                        _toastText.value = Event(result.error)
                    }
                } else {
                    val result = Result.Error(getErrorBodyMessage(response))
                    tokenResult.value = result
                    _toastText.value = Event(result.error)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val result = Result.Error(t.message.toString())
                tokenResult.value = result
                _toastText.value = Event(result.error)
            }
        })
        return tokenResult
    }

    fun getRegisterResult(
        username: String,
        email: String,
        password: String
    ): LiveData<Result<String>> {
        isRegistered.value = Result.Loading

        val register = apiService.register(username, email, password)
        register.enqueue(object : Callback<MainResponse> {
            override fun onResponse(
                call: Call<MainResponse>,
                response: Response<MainResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        isRegistered.value = Result.Success(responseBody.message)
                        _toastText.value = Event(responseBody.message)
                    } else {
                        val result = Result.Error(getErrorBodyMessage(response))
                        isRegistered.value = result
                        _toastText.value = Event(result.error)
                    }
                } else {
                    val result = Result.Error(getErrorBodyMessage(response))
                    isRegistered.value = result
                    _toastText.value = Event(result.error)
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                val result = Result.Error(t.message.toString())
                isRegistered.value = result
                _toastText.value = Event(result.error)
            }
        })

        return isRegistered
    }

    companion object {
        private var TAG = AuthRepository::class.java.simpleName

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }
}
