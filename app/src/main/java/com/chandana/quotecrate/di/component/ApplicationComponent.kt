package com.chandana.quotecrate.di.component

import android.content.Context
import com.chandana.quotecrate.QuoteCrateApplication
import com.chandana.quotecrate.data.api.NetworkService
import com.chandana.quotecrate.di.ApplicationContext
import com.chandana.quotecrate.di.module.ApplicationModule
import com.chandana.quotecrate.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(application: QuoteCrateApplication)

    @ApplicationContext
    fun getContext(): Context
    fun getNetworkService(): NetworkService
    fun getFirebaseAuth(): FirebaseAuth
    fun getDispatcherProvider(): DispatcherProvider
}