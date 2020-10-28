package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Article
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Articles
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.ServerError
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.normalizeToNetworkError
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.toDomain
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

fun <F> Runtime<F>.loadNews(): Kind<F, Articles> = bindingConcurrent {
    val response = !fetchNews()

    continueOn(ctx.computationDispatcher)
    response.articles.takeIf { it.isNotEmpty() }?.toDomain()
            ?: !raiseError<Articles>(ServerError.RemoteArticlesResponseIsEmpty)
}

fun <F> Runtime<F>.loadNewsItemDetails(title: String): Kind<F, Article> = bindingConcurrent {
    val response = !fetchNews()

    continueOn(ctx.computationDispatcher)
    response.articles.find { it.title == title }?.toDomain()
            ?: !raiseError<Article>(ServerError.ArticleNotInRemoteStorage)
}

private fun <F> Runtime<F>.fetchNews() = effect(ctx.bgDispatcher) {
    ctx.newsService.fetchNews("android")
}.handleErrorWith { e -> raiseError(e.normalizeToNetworkError()) }
