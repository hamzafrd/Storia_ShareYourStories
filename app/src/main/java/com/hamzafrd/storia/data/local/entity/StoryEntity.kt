package com.hamzafrd.storia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "stories")
data class StoryEntity(
    @field:ColumnInfo("id")
    @field:PrimaryKey
    val id: String,

    @field:ColumnInfo("createdAt")
    val createdAt: String,

    @field:ColumnInfo("photoUrl")
    val photoUrl: String,

    @field:ColumnInfo("name")
    val name: String,

    @field:ColumnInfo("description")
    val description: String,

    @field:ColumnInfo("lon")
    val lon: Double? = null,

    @field:ColumnInfo("lat")
    val lat: Double? = null,

)