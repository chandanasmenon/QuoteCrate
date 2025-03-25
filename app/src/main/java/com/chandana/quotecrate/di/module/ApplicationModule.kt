package com.chandana.quotecrate.di.module

import com.chandana.quotecrate.data.api.NetworkService
import com.chandana.quotecrate.di.BaseUrl
import com.chandana.quotecrate.di.NetworkApiKey
import com.chandana.quotecrate.utils.AppConstant
import com.chandana.quotecrate.utils.DefaultDispatcherProvider
import com.chandana.quotecrate.utils.DispatcherProvider
import com.chandana.quotecrate.utils.HeaderInterceptor
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @BaseUrl
    @Provides
    fun provideBaseUrl(): String = "https://api.api-ninjas.com/v1/"

    @Provides
    @NetworkApiKey
    fun provideApiKey(): String = AppConstant.API_KEY

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideHeaderInterceptor(
        @NetworkApiKey apiKey: String
    ): HeaderInterceptor = HeaderInterceptor(apiKey)

    @Provides
    @Singleton
    fun provideOkHttpClient(headerInterceptor: HeaderInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideNetworkService(
        @BaseUrl baseUrl: String,
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): NetworkService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(NetworkService::class.java)
    }
}