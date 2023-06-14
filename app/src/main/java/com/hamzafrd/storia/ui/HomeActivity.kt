package com.hamzafrd.storia.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamzafrd.storia.R
import com.hamzafrd.storia.databinding.ActivityHomeBinding
import com.hamzafrd.storia.helper.AuthViewModelFactory
import com.hamzafrd.storia.helper.SessionPreferences
import com.hamzafrd.storia.helper.StoryViewModelFactory
import com.hamzafrd.storia.ui.adapter.LoadingStateAdapter
import com.hamzafrd.storia.ui.adapter.StoryPagingAdapter
import com.hamzafrd.storia.ui.viewModel.AuthViewModel
import com.hamzafrd.storia.ui.viewModel.StoryViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var storyAdapter: StoryPagingAdapter
    private lateinit var viewModelStory: StoryViewModel
    private lateinit var viewModelAuth: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        getPagedStories()
    }

    override fun onResume() {
        super.onResume()
        storyAdapter.refresh()
    }

    private fun setViewModel() {
        val pref = SessionPreferences.getInstance(dataStore)
        val sFactory = StoryViewModelFactory.getInstance(this, pref)
        viewModelStory = viewModels<StoryViewModel> { sFactory }.value

        val aFactory = AuthViewModelFactory.getInstance(pref)
        viewModelAuth = viewModels<AuthViewModel> { aFactory }.value
    }

    private fun getPagedStories() {
        storyAdapter = StoryPagingAdapter { story ->
            Intent(this, DetailStoryActivity::class.java).also {
                it.putExtra(EXTRA_ID, story.id)
                startActivity(
                    it,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(binding.root.context as Activity)
                        .toBundle()
                )
            }
        }

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }

        viewModelStory.pagedStories.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModelAuth.deleteSettings()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            R.id.action_add_tory -> {
                Intent(this, AddStoryActivity::class.java).also {
                    startActivity(it)
                }
                true
            }

            R.id.action_location -> {
                Intent(this, MapsActivity::class.java).also {
                    startActivity(it)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}
