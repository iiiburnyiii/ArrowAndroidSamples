package com.github.jorgecastillo.kotlinandroid.io.algebras.business.model

import com.github.jorgecastillo.kotlinandroid.io.algebras.data.LocalStorageError
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.ServerError

sealed class DomainError : Throwable() {

    companion object {
        fun fromThrowable(e: Throwable): DomainError =
            when (e) {
                is ServerError.UserIsUnauthorized -> AuthenticationError
                is ServerError.RemoteArticlesResponseIsEmpty, is LocalStorageError.LocalArticlesStorageIsEmpty ->
                    EmptyError
                is ServerError.ArticleNotInRemoteStorage, is LocalStorageError.ArticleNotInLocalStorage ->
                    NotFoundError
                else -> UnknownError(e)
            }
    }

    object AuthenticationError : DomainError()
    object NotFoundError : DomainError()
    object EmptyError : DomainError()
    class UnknownError(override val cause: Throwable?) : DomainError()
}
