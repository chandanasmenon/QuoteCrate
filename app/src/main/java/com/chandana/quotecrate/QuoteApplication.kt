package com.chandana.quotecrate

import android.app.Application
import com.chandana.quotecrate.di.component.ApplicationComponent
import com.chandana.quotecrate.di.component.DaggerApplicationComponent
import com.chandana.quotecrate.di.module.ApplicationModule

class QuoteCrateApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        injectDependencies()
    }

    private fun injectDependencies() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}