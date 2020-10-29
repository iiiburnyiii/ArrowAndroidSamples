package com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter.ArticlesAdapter.ViewHolder
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.ArticleViewState
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_news.view.*

class ArticlesAdapter(
        var news: List<ArticleViewState> = ArrayList(),
        val itemClick: (ArticleViewState) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(news[pos])
    }

    override fun getItemCount() = news.size

    class ViewHolder(
        view: View,
        val itemClick: (ArticleViewState) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(article: ArticleViewState) {
            with(article) {
                Picasso.get().load(photoUrl).into(itemView.picture)
                itemView.title.text = article.title
                itemView.description.text = article.description
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}
