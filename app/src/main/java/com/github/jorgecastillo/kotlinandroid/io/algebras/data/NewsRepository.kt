package com.github.jorgecastillo.kotlinandroid.io.algebras.data

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Article
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Articles
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.CachePolicy.*
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.local.populateLocalStorage
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.local.queryNews
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.local.queryNewsItemDetails
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.loadNews
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.loadNewsItemDetails
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

sealed class CachePolicy {
    object NetworkOnly : CachePolicy()
    object NetworkFirst : CachePolicy()
    object LocalOnly : CachePolicy()
    object LocalFirst : CachePolicy()
}

fun <F> Runtime<F>.getArticlesWithCachePolicy(policy: CachePolicy): Kind<F, Articles> =
    when (policy) {
        NetworkOnly -> loadNews()
        NetworkFirst -> loadNews().flatTap(this::populateLocalStorage)
        LocalOnly -> queryNews()
        LocalFirst -> queryNews().handleErrorWith { e ->
            when (e) {
                is LocalStorageError.LocalArticlesStorageIsEmpty -> loadNews().flatTap(this::populateLocalStorage)
                else -> raiseError(LocalStorageError.UnknownDataStorageError(e))
            }
        }
    }

fun <F> Runtime<F>.getArticleDetailsWithCachePolicy(policy: CachePolicy, title: String): Kind<F, Article> =
    when (policy) {
        NetworkOnly -> loadNewsItemDetails(title)
        NetworkFirst -> loadNewsItemDetails(title).flatTap { article ->
            populateLocalStorage(listOf(article))
        }
        LocalOnly -> queryNewsItemDetails(title)
        LocalFirst -> queryNewsItemDetails(title).handleErrorWith { e ->
            when (e) {
                is LocalStorageError.ArticleNotInLocalStorage -> loadNewsItemDetails(title).flatTap { article ->
                    populateLocalStorage(listOf(article))
                }
                else -> raiseError(LocalStorageError.UnknownDataStorageError(e))
            }
        }
    }
