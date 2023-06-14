package com.hamzafrd.storia.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hamzafrd.storia.data.repository.StoryRepository
import com.hamzafrd.storia.di.Injection
import com.hamzafrd.storia.ui.viewModel.StoryViewModel


class StoryViewModelFactory(
    private val storyRepository: StoryRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null

        fun getInstance(context: Context, pref: SessionPreferences): StoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: StoryViewModelFactory(Injection.provideRepository(context, pref))
            }.also { instance = it }
    }
}