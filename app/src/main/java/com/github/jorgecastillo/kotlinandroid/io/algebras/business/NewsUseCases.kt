package com.github.jorgecastillo.kotlinandroid.io.algebras.business

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Article
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Articles
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.CachePolicy
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.getArticleDetailsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.getArticlesWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

fun <F> Runtime<F>.getNews(): Kind<F, Articles> =
    getArticlesWithCachePolicy(CachePolicy.NetworkOnly)

fun <F> Runtime<F>.getArticleDetails(title: String): Kind<F, Article> =
    getArticleDetailsWithCachePolicy(CachePolicy.NetworkOnly, title)
