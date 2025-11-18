package com.medassist.app.ui.screens.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medassist.app.R

/**
 * RecyclerView adapter for displaying health articles
 */
class ArticlesAdapter(
    private val articles: List<Article>,
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.articleTitle)
        val authorText: TextView = itemView.findViewById(R.id.articleAuthor)
        val summaryText: TextView = itemView.findViewById(R.id.articleSummary)
        val dateText: TextView = itemView.findViewById(R.id.articleDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        
        holder.titleText.text = article.title
        holder.authorText.text = "By ${article.author}"
        holder.summaryText.text = article.summary
        holder.dateText.text = article.date
        
        holder.itemView.setOnClickListener {
            onItemClick(article)
        }
    }

    override fun getItemCount(): Int = articles.size
}

