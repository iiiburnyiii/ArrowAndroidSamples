package com.github.jorgecastillo.kotlinandroid.io.algebras.business

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Article
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.CachePolicy
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.getNewsItemDetailsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.getNewsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

fun <F> Runtime<F>.getNews(): Kind<F, List<Article>> =
    getNewsWithCachePolicy(CachePolicy.NetworkOnly)

fun <F> Runtime<F>.getNewsItemDetails(title: String): Kind<F, Article> =
    getNewsItemDetailsWithCachePolicy(CachePolicy.NetworkOnly, title)
