package com.github.jorgecastillo.kotlinandroid.io.algebras.business.model

typealias Articles = List<Article>

data class Article(
    val source: String,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String
)
