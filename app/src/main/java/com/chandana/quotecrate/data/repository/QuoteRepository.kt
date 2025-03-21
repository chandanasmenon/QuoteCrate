package com.chandana.quotecrate.data.repository

import com.chandana.quotecrate.data.api.NetworkService
import com.chandana.quotecrate.data.model.QuoteDataItem
import com.chandana.quotecrate.di.ActivityScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityScope
class QuoteRepository @Inject constructor(private val networkService: NetworkService) {
    fun getRandomQuote(): Flow<QuoteDataItem> {
        return flow {
            emit(networkService.getRandomQuote())
        }.map { it[0] }
    }
}