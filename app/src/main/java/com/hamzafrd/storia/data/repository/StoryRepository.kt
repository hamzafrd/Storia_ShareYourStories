package com.hamzafrd.storia.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.hamzafrd.storia.data.Result
import com.hamzafrd.storia.data.StoryRemoteMediator
import com.hamzafrd.storia.data.local.entity.StoryEntity
import com.hamzafrd.storia.data.local.room.StoryDatabase
import com.hamzafrd.storia.data.StoriesPlace
import com.hamzafrd.storia.data.remote.response.DetailResponse
import com.hamzafrd.storia.data.remote.response.ListStoryResponse
import com.hamzafrd.storia.data.remote.response.MainResponse
import com.hamzafrd.storia.data.remote.retrofit.ApiService
import com.hamzafrd.storia.utils.Event
import com.hamzafrd.storia.utils.getErrorBodyMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoryRepository private constructor(
    private val api: ApiService,
    private val database: StoryDatabase,
) {
    private val detailResult = MediatorLiveData<Result<List<String>>>()
    private val uploadResult = MediatorLiveData<Result<String>>()

    private val locationResult = MediatorLiveData<Result<List<StoriesPlace>>>()

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun setToastText(text: String) {
        _toastText.value = Event(text)
    }

    fun getPagingStories(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(database, api),
            pagingSourceFactory = {
                database.storyDao().getAllPagedStory()
            }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<Result<List<StoriesPlace>>> {
        locationResult.value = Result.Loading
        val client = api.getLocationStories()
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory
                    val listPlace = ArrayList<StoriesPlace>()
                    stories?.forEach {
                        val place = StoriesPlace(
                            it.name,
                            it.lat as Double,
                            it.lon as Double
                        )
                        listPlace.add(place)
                    }

                    locationResult.value = Result.Success(listPlace)
                }

            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                locationResult.value = Result.Error(t.message.toString())
            }
        })

        return locationResult
    }

    fun getDetailStories(id: String): LiveData<Result<List<String>>> {
        detailResult.value = Result.Loading
        val client = api.getDetailStories(id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                if (response.isSuccessful) {
                    val resultBody = response.body()
                    if (resultBody != null) {
                        val story = resultBody.story
                        detailResult.value = Result.Success(
                            listOf(
                                story.id,
                                story.name,
                                story.photoUrl,
                                story.description,
                                story.createdAt
                            )
                        )
                    } else {
                        detailResult.value = Result.Error(getErrorBodyMessage(response))
                    }
                } else {
                    detailResult.value = Result.Error(getErrorBodyMessage(response))
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                detailResult.value = Result.Error(t.message.toString())
            }

        })
        return detailResult
    }

    fun postStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
    ): LiveData<Result<String>> {
        uploadResult.value = Result.Loading
        val uploadImageRequest =
            api.uploadImage(imageMultipart, description, lat, lon)
        uploadImageRequest.enqueue(object : Callback<MainResponse> {
            override fun onResponse(
                call: Call<MainResponse>,
                response: Response<MainResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        uploadResult.value = Result.Success(responseBody.message)
                        Log.d("test on repo" ,responseBody.message)
                    } else {
                        uploadResult.value = Result.Error(getErrorBodyMessage(response))
                    }
                } else {
                    uploadResult.value = Result.Error(getErrorBodyMessage(response))
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                uploadResult.value = Result.Error(t.message.toString())
            }
        })
        return uploadResult
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            api: ApiService,
            database: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(api, database)
            }.also { instance = it }
    }
}
