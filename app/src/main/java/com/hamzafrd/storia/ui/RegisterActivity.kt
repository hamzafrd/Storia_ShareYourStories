package com.hamzafrd.storia.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.snackbar.Snackbar
import com.hamzafrd.storia.data.Result
import com.hamzafrd.storia.databinding.ActivityRegisterBinding
import com.hamzafrd.storia.helper.AuthViewModelFactory
import com.hamzafrd.storia.helper.SessionPreferences
import com.hamzafrd.storia.ui.viewModel.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SessionPreferences.getInstance(dataStore)
        val factory = AuthViewModelFactory.getInstance(pref)
        val viewModel: AuthViewModel by viewModels { factory }

        viewModel.getToastText().observe(this) { event ->
            event.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            viewModel.register(
                binding.nameEditText.text.toString(),
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            ).observe(this@RegisterActivity) {
                if (it != null) {
                    when (it) {
                        is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            finish()
                        }

                        is Result.Error -> binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
}