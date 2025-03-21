package com.app.assignment

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication: Application(){

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
    }

    companion object {
        var context: Context? = null
    }

}