package com.github.jorgecastillo.kotlinandroid.io.algebras.data.local

import arrow.Kind
import arrow.core.extensions.set.foldable.toList
import arrow.core.rightIfNotNull
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Article
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.Articles
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.LocalStorageError
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

private val localNewsStorage = mutableSetOf<Article>()

fun <F> Runtime<F>.populateLocalStorage(news: Articles): Kind<F, Unit> = laterOrRaise(ctx.computationDispatcher) {
    localNewsStorage.addAll(news).takeIf { it }?.let { Unit }
            .rightIfNotNull { LocalStorageError.LocalArticlesStoragePopulatingFailed }
}

fun <F> Runtime<F>.queryNews(): Kind<F, Articles> = laterOrRaise(ctx.computationDispatcher) {
    localNewsStorage.takeIf { it.isNotEmpty() }?.toList()
            .rightIfNotNull { LocalStorageError.LocalArticlesStorageIsEmpty }
}

fun <F> Runtime<F>.queryNewsItemDetails(title: String): Kind<F, Article> = laterOrRaise(ctx.computationDispatcher) {
    localNewsStorage.find { it.title == title }
            .rightIfNotNull { LocalStorageError.ArticleNotInLocalStorage }
}
