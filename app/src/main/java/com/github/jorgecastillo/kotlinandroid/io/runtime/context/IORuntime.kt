package com.github.jorgecastillo.kotlinandroid.io.runtime.context

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.IOConcurrent
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ConcurrentFx
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.NewsApiService
import kotlinx.coroutines.CoroutineDispatcher

/**
 * This context contains the program dependencies. It can potentially work over any data type F that
 * supports concurrency, or in other words, any data type F that there's an instance of concurrent
 * Fx for.
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
abstract class Runtime<F>(
    C: Concurrent<F>,
    val ctx: RuntimeContext
) : Concurrent<F> by C

fun IO.Companion.runtime(ctx: RuntimeContext) =
        object : Runtime<ForIO>(IO.concurrent(), ctx) {}

data class RuntimeContext(
    val bgDispatcher: CoroutineDispatcher,
    val mainDispatcher: CoroutineDispatcher,
    val computationDispatcher: CoroutineDispatcher,
    val newsService: NewsApiService
)
