package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arrow.fx.IO
import arrow.integrations.kotlinx.suspendCancellable
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.ArticlesListView
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter.ArticlesAdapter
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.getAllArticles
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.ArticleViewState
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.onArticleClick
import com.github.jorgecastillo.kotlinandroid.io.runtime.application
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.runtime
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch

class AndroidArticlesActivity : AppCompatActivity(), ArticlesListView {

    private lateinit var adapter: ArticlesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupList()
    }

    private fun setupList() {
        newsList.setHasFixedSize(true)
        newsList.layoutManager = LinearLayoutManager(this)
        adapter = ArticlesAdapter(itemClick = onArticleClick())
        newsList.adapter = adapter
    }

    private fun onArticleClick(): (ArticleViewState) -> Unit = { articleViewState: ArticleViewState ->
        lifecycleScope.launch {
            IO.runtime(application().runtimeContext)
                    .onArticleClick(
                            ctx = this@AndroidArticlesActivity,
                            title = articleViewState.title
                    ).suspendCancellable()
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            IO.runtime(application().runtimeContext)
                    .getAllArticles(view = this@AndroidArticlesActivity).suspendCancellable()
        }
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loader.visibility = View.GONE
    }

    override fun drawArticles(news: List<ArticleViewState>) {
        adapter.news = news
        adapter.notifyDataSetChanged()
    }

    override fun showNotFoundError() {
        Snackbar.make(newsList, R.string.not_found, Snackbar.LENGTH_SHORT).show()
    }

    override fun showGenericError() {
        Snackbar.make(newsList, R.string.generic, Snackbar.LENGTH_SHORT).show()
    }

    override fun showAuthenticationError() {
        Snackbar.make(newsList, R.string.authentication, Snackbar.LENGTH_SHORT).show()
    }
}

