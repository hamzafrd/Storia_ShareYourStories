package com.hamzafrd.storia.data.local.room

import androidx.paging.PagingSource
import androidx.room.*
import com.hamzafrd.storia.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPagedStory(quote: List<StoryEntity>)

    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllPagedStory(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM stories")
    suspend fun deleteAllPagedStories()
}