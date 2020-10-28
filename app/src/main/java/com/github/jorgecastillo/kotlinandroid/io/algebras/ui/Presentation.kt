package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import android.util.Log
import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.getNews
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.getNewsItemDetails
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Article
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.DomainError
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.NewsItemViewState
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

interface NewsView {

    fun showLoading(): Unit

    fun hideLoading(): Unit

    fun showNotFoundError(): Unit

    fun showGenericError(): Unit

    fun showAuthenticationError(): Unit
}

interface NewsListView : NewsView {

    fun drawNews(news: List<NewsItemViewState>): Unit
}

interface NewsItemDetailView : NewsView {

    fun drawNewsItem(newsItem: NewsItemViewState)
}

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. We'll end up running these methods using a valid F type that support Concurrent behaviors,
 * like IO.
 */
fun <F> Runtime<F>.onNewsItemClick(
    ctx: Context,
    title: String
): Kind<F, Unit> =
    goToNewsItemDetail(ctx, title)

private fun displayErrors(
    view: NewsView,
    t: Throwable
): Unit {
    when (DomainError.fromThrowable(t)) {
        is DomainError.NotFoundError -> view.showNotFoundError()
        is DomainError.UnknownError -> view.showGenericError()
        is DomainError.AuthenticationError -> view.showAuthenticationError()
        is DomainError.EmptyError -> view.showGenericError()
    }
}

fun <F> Runtime<F>.getAllNews(view: NewsListView): Kind<F, Unit> = bindingConcurrent {
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
                view.drawNews(it.map { newsItem -> newsItem.toViewState() })
            }
        )
    }
}

fun <F> Runtime<F>.getNewsItemDetails(
    title: String,
    view: NewsItemDetailView
): Kind<F, Unit> = bindingConcurrent {
    !effect { view.showLoading() }
    val maybeNewsItem = !getNewsItemDetails(title).attempt()
    continueOn(ctx.mainDispatcher)
    !effect { view.hideLoading() }
    !effect {
        maybeNewsItem.fold(
            ifLeft = { displayErrors(view, it) },
            ifRight = { newsItem -> view.drawNewsItem(newsItem.toViewState()) }
        )
    }
}

fun Article.toViewState() = NewsItemViewState(
    title = title,
    author = author,
    photoUrl = urlToImage,
    publishedAt = publishedAt,
    description = description,
    content = content
)
