package com.hamzafrd.storia.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.hamzafrd.storia.R
import com.hamzafrd.storia.data.Result
import com.hamzafrd.storia.databinding.ActivityDetailStoryBinding
import com.hamzafrd.storia.helper.SessionPreferences
import com.hamzafrd.storia.helper.StoryViewModelFactory
import com.hamzafrd.storia.ui.viewModel.StoryViewModel
import com.hamzafrd.storia.utils.DateFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SessionPreferences.getInstance(dataStore)
        val factory = StoryViewModelFactory.getInstance(this, pref)
        val viewModel: StoryViewModel by viewModels { factory }

        val id = intent.getStringExtra(HomeActivity.EXTRA_ID)
        if (!id.isNullOrEmpty()) {

            viewModel.getToastText().observe(this) { event ->
                event.getContentIfNotHandled()?.let { text ->
                    Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                }
            }

            viewModel.getDetailsStories(id).observe(this) {
                when (it) {
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        viewModel.setToastText(it.error)
                    }

                    Result.Loading -> binding.progressBar.visibility = View.VISIBLE

                    is Result.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            usernameDetail.text = it.data[1]
                            Glide.with(imageUrlDetail).load(it.data[2]).apply(
                                RequestOptions
                                    .placeholderOf(R.drawable.ic_loading)
                                    .error(R.drawable.ic_error)
                            )
                                .into(imageUrlDetail)
                            descStoryDetail.text = it.data[3]
                            dateDetail.text = DateFormatter.formatDate(it.data[4])

                        }
                        viewModel.setToastText("Data Fetched Successfully")
                    }
                }
            }
        }
    }
}