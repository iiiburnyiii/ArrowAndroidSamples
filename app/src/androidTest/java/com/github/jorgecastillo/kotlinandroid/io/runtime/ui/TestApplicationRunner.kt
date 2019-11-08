package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class MockableApplicationTestRunner : AndroidJUnitRunner() {

    @Throws(
            InstantiationException::class,
            IllegalAccessException::class,
            ClassNotFoundException::class
    )
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, CustomTestApplication::class.java!!.getName(), context)
    }
}
