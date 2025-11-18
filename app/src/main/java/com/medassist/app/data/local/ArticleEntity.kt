package com.medassist.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Articles
 * 
 * Stores articles locally for offline access.
 * This entity maps to the Article data class from the API.
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val summary: String,
    val content: String,
    val imageUrl: String,
    val date: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

