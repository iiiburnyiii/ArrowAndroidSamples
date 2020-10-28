package com.github.jorgecastillo.kotlinandroid.io.algebras.data

sealed class ServerError : Throwable() {
    object UserIsUnauthorized : ServerError()

    object RemoteArticlesResponseIsEmpty : ServerError()
    object ArticleNotInRemoteStorage : ServerError()

    object UnknownServerError : ServerError()
}

sealed class LocalStorageError : Throwable() {
    object LocalArticlesStoragePopulatingFailed : LocalStorageError()
    object LocalArticlesStorageIsEmpty : LocalStorageError()
    object ArticleNotInLocalStorage : LocalStorageError()

    class UnknownDataStorageError(override val cause: Throwable?) : LocalStorageError()
}
