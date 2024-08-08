package com.example.safecircle

import android.app.Application

class SafeCircleApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPref.init(this)
    }
}