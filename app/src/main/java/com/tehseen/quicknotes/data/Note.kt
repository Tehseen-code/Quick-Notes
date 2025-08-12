package com.tehseen.quicknotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis()
)
