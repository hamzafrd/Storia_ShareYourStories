package com.hamzafrd.storia.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hamzafrd.storia.data.local.entity.StoryEntity
import com.hamzafrd.storia.data.repository.StoryRepository
import com.hamzafrd.storia.helper.SessionPreferences
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    val pagedStories: LiveData<PagingData<StoryEntity>> =
        storyRepository.getPagingStories().cachedIn(viewModelScope)

    fun getStoriesLocation() = storyRepository.getStoriesWithLocation()
    fun getDetailsStories(id: String) = storyRepository.getDetailStories(id)
    fun uploadStory(
        image: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
    ) = storyRepository.postStory(image, description, lat, lon)

    fun getToastText() = storyRepository.toastText
    fun setToastText(text: String) = storyRepository.setToastText(text)
}