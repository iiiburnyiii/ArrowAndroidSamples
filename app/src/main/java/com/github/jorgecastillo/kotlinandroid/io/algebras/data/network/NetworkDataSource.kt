package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.NewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.findNewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.normalizeError
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.toDomain
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.toNetworkError
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime
import retrofit2.HttpException

fun <F> Runtime<F>.loadNews(): Kind<F, List<NewsItem>> = bindingConcurrent {
    val response = !effect(ctx.bgDispatcher) { fetchNews() }

    continueOn(ctx.computationDispatcher)
    response.articles.toDomain()
}.handleErrorWith(this::raiseNetworkOrNormalizedError)

fun <F> Runtime<F>.loadNewsItemDetails(title: String): Kind<F, NewsItem> = bindingConcurrent {
    val response = !effect(ctx.bgDispatcher) { fetchNews() }
    continueOn(ctx.computationDispatcher)

    response.articles.findNewsItem(title).fold(
            ifLeft = { !raiseError<NewsItem>(it) },
            ifRight = { it }
    )
}.handleErrorWith(this::raiseNetworkOrNormalizedError)

private suspend fun <F> Runtime<F>.fetchNews() = ctx.newsService.fetchNews("android")

private fun <F, A> Runtime<F>.raiseNetworkOrNormalizedError(error: Throwable): Kind<F, A> = when (error) {
    is HttpException -> error.code().toNetworkError()
    else -> error.normalizeError()
}.raiseError()
