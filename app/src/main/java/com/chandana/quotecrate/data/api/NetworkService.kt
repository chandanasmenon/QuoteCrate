package com.chandana.quotecrate.data.api

import com.chandana.quotecrate.data.model.QuoteDataItem
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface NetworkService {
    @GET("quotes")
    suspend fun getRandomQuote(): List<QuoteDataItem>
}