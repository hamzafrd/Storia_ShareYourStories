package com.hamzafrd.storia.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.snackbar.Snackbar
import com.hamzafrd.storia.data.Result
import com.hamzafrd.storia.databinding.ActivityLoginBinding
import com.hamzafrd.storia.helper.AuthViewModelFactory
import com.hamzafrd.storia.helper.SessionPreferences
import com.hamzafrd.storia.ui.viewModel.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addAnimation()
        val pref = SessionPreferences.getInstance(dataStore)
        val factory = AuthViewModelFactory.getInstance(pref)
        val viewModel: AuthViewModel by viewModels { factory }

        viewModel.getToastText().observe(this) { event ->
            event.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }


        binding.apply {
            btnLogin.setOnClickListener {
                viewModel.login(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                ).observe(this@LoginActivity) { loggedin ->
                    if (loggedin != null) {
                        when (loggedin) {
                            is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                viewModel.saveSessionTokenSettings(loggedin.data)
                                Intent(this@LoginActivity, HomeActivity::class.java).also {
                                    startActivity(it)
                                    finish()
                                }
                            }

                            is Result.Error -> binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
            btnRegister.setOnClickListener {
                Intent(this@LoginActivity, RegisterActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun addAnimation() {
        ObjectAnimator.ofFloat(binding.tvLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val loginButton = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val registerButton =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                emailEditTextLayout,
                passwordEditTextLayout,
                loginButton,
                registerButton
            )
        }.start()
    }

}