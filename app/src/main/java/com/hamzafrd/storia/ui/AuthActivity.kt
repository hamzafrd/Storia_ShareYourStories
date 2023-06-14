package com.hamzafrd.storia.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hamzafrd.storia.helper.AuthViewModelFactory
import com.hamzafrd.storia.helper.SessionPreferences
import com.hamzafrd.storia.ui.viewModel.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val pref = SessionPreferences.getInstance(dataStore)
        val factory = AuthViewModelFactory.getInstance(pref)
        val viewModel: AuthViewModel by viewModels { factory }

        viewModel.getTokenSetting().observe(this) { token ->
            val state =
                if (!token.isNullOrEmpty()) HomeActivity::class.java else LoginActivity::class.java
            Intent(this,state).also {
                startActivity(it)
                finish()
            }
        }
    }
}