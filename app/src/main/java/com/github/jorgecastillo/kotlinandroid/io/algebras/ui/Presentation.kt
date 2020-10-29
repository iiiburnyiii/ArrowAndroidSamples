package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import android.util.Log
import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.getNews
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.getArticleDetails
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Article
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.DomainError
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.ArticleViewState
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

interface ArticlesView {

    fun showLoading(): Unit

    fun hideLoading(): Unit

    fun showNotFoundError(): Unit

    fun showGenericError(): Unit

    fun showAuthenticationError(): Unit
}

interface ArticlesListView : ArticlesView {

    fun drawArticles(news: List<ArticleViewState>): Unit
}

interface ArticlesItemDetailView : ArticlesView {

    fun drawArticle(article: ArticleViewState)
}

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. We'll end up running these methods using a valid F type that support Concurrent behaviors,
 * like IO.
 */
fun <F> Runtime<F>.onArticleClick(
    ctx: Context,
    title: String
): Kind<F, Unit> =
    goToNewsItemDetail(ctx, title)

private fun displayErrors(
        view: ArticlesView,
        t: Throwable
): Unit {
    when (DomainError.fromThrowable(t)) {
        is DomainError.NotFoundError -> view.showNotFoundError()
        is DomainError.UnknownError -> view.showGenericError()
        is DomainError.AuthenticationError -> view.showAuthenticationError()
        is DomainError.EmptyError -> view.showGenericError()
    }
}

fun <F> Runtime<F>.getAllArticles(view: ArticlesListView): Kind<F, Unit> = bindingConcurrent {
    !effect { view.showLoading() }
    val maybeNews = !getNews().attempt()
    continueOn(ctx.mainDispatcher)
    !effect { view.hideLoading() }
    !effect {
        maybeNews.fold(
            ifLeft = {
                Log.w("Pres", "News error: $it")
                displayErrors(view, it)
            },
            ifRight = {
                Log.d("Pres", "News: $it")
                view.drawArticles(it.map { newsItem -> newsItem.toViewState() })
            }
        )
    }
}

fun <F> Runtime<F>.getArticleDetails(
    title: String,
    view: ArticlesItemDetailView
): Kind<F, Unit> = bindingConcurrent {
    !effect { view.showLoading() }
    val maybeNewsItem = !getArticleDetails(title).attempt()
    continueOn(ctx.mainDispatcher)
    !effect { view.hideLoading() }
    !effect {
        maybeNewsItem.fold(
            ifLeft = { displayErrors(view, it) },
            ifRight = { newsItem -> view.drawArticle(newsItem.toViewState()) }
        )
    }
}

fun Article.toViewState() = ArticleViewState(
    title = title,
    author = author,
    photoUrl = urlToImage,
    publishedAt = publishedAt,
    description = description,
    content = content
)
