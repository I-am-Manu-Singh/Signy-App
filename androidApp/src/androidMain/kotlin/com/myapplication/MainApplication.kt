package com.myapplication

import android.app.Application
import com.myapplication.common.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            androidContext(this@MainApplication)
        }
    }
}
