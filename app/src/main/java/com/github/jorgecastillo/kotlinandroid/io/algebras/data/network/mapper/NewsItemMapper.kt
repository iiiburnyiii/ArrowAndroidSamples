package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.NewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.dto.NetworkNewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.error.NetworkError

fun List<NetworkNewsItem>.toDomain() = map { it.toDomain() }

fun NetworkNewsItem.toDomain() = NewsItem(
    source = source.name,
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)

fun List<NetworkNewsItem>.findNewsItem(title: String): Either<Throwable, NewsItem> =
        find { it.title == title }?.toDomain()?.right() ?: NetworkError.NotFound.left()
