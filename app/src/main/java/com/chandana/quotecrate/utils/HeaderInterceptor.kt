package com.chandana.quotecrate.utils

import com.chandana.quotecrate.utils.AppConstant.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("X-Api-Key", API_KEY)
            .build()
        return chain.proceed(newRequest)
    }
}