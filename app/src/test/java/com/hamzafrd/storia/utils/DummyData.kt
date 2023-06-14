package com.hamzafrd.storia.utils

import com.hamzafrd.storia.data.local.entity.StoryEntity

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                i.toString(),
                "2022-01-08T06:34:18.598Z",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "name $i",
                "description $i"
            )
            items.add(quote)
        }
        return items
    }
}