package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.fx.IO
import arrow.integrations.kotlinx.suspendCancellable
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.R.string
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.ArticlesItemDetailView
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.extensions.loadImageAsync
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.getArticleDetails
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.ArticleViewState
import com.github.jorgecastillo.kotlinandroid.io.runtime.application
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.runtime
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.launch

class ArticleDetailActivity : AppCompatActivity(), ArticlesItemDetailView {

    companion object {
        const val EXTRA_NEWS_ID = "EXTRA_ID"

        fun launch(
            source: Context,
            newsId: String
        ) {
            val intent = Intent(source, ArticleDetailActivity::class.java)
            intent.putExtra(EXTRA_NEWS_ID, newsId)
            source.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }

    override fun onResume() {
        super.onResume()
        intent.extras?.let {
            val newsId = it.getString(EXTRA_NEWS_ID)
            if (newsId == null) {
                closeWithError()
            } else {
                loadNewsItemDetails(newsId)
            }
        } ?: closeWithError()
    }

    private fun loadNewsItemDetails(title: String) {
        lifecycleScope.launch {
            IO.runtime(application().runtimeContext).getArticleDetails(
                    title = title,
                    view = this@ArticleDetailActivity
            ).suspendCancellable()
        }
    }

    private fun closeWithError() {
        Toast.makeText(this, string.news_id_needed, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loader.visibility = View.GONE
    }

    override fun drawArticle(article: ArticleViewState) {
        collapsingToolbar.title = article.title
        description.text = article.description ?: getString(string.empty_description)
        article.photoUrl?.let { url -> headerImage.loadImageAsync(url) }
    }

    override fun showNotFoundError() {
        Snackbar.make(appBar, string.not_found, Snackbar.LENGTH_SHORT).show()
    }

    override fun showGenericError() {
        Snackbar.make(appBar, string.generic, Snackbar.LENGTH_SHORT).show()
    }

    override fun showAuthenticationError() {
        Snackbar.make(appBar, string.authentication, Snackbar.LENGTH_SHORT).show()
    }
}
