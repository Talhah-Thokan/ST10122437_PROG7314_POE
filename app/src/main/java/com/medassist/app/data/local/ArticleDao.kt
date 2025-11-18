package com.medassist.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Articles
 * 
 * Provides methods to interact with the articles table in Room database.
 */
@Dao
interface ArticleDao {
    
    /**
     * Get all articles from local database
     * Returns Flow for reactive updates
     */
    @Query("SELECT * FROM articles ORDER BY date DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>
    
    /**
     * Get all articles synchronously (for one-time reads)
     */
    @Query("SELECT * FROM articles ORDER BY date DESC")
    suspend fun getAllArticlesSync(): List<ArticleEntity>
    
    /**
     * Get article by ID
     */
    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: String): ArticleEntity?
    
    /**
     * Insert a single article
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)
    
    /**
     * Insert multiple articles
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)
    
    /**
     * Delete all articles
     */
    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()
    
    /**
     * Delete a specific article
     */
    @Delete
    suspend fun deleteArticle(article: ArticleEntity)
    
    /**
     * Update last updated timestamp
     */
    @Query("UPDATE articles SET lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: String, timestamp: Long)
}

