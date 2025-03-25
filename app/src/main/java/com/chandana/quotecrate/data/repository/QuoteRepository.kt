package com.chandana.quotecrate.data.repository

import com.chandana.quotecrate.data.api.NetworkService
import com.chandana.quotecrate.data.model.QuoteDataItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuoteRepository @Inject constructor(private val networkService: NetworkService) {
    fun getRandomQuote(): Flow<QuoteDataItem> {
        return flow {
            emit(networkService.getRandomQuote())
        }.map { response ->
            response[0]
        }
    }
}