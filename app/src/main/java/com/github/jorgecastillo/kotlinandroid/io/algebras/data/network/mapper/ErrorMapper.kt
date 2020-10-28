package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper

import com.github.jorgecastillo.kotlinandroid.io.algebras.data.ServerError
import retrofit2.HttpException

fun Throwable.normalizeToNetworkError() = when (this) {
    is ServerError -> this
    is HttpException -> toNetworkError()
    else -> ServerError.UnknownServerError
}

private fun HttpException.toNetworkError() = when (this.code()) {
    401 -> ServerError.UserIsUnauthorized
    else -> ServerError.UnknownServerError
}
