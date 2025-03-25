package com.chandana.quotecrate.repository

import app.cash.turbine.test
import com.chandana.quotecrate.data.api.NetworkService
import com.chandana.quotecrate.data.model.QuoteDataItem
import com.chandana.quotecrate.data.repository.QuoteRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class QuoteRepositoryTest {
    @Mock
    lateinit var networkService: NetworkService

    private lateinit var repository: QuoteRepository

    @Before
    fun setUp() {
        repository = QuoteRepository(networkService)
    }

    @Test
    fun getRandomQuote_whenNetworkServiceResponseSuccess_shouldReturnSuccess() {
        runTest {
            val quoteList = mutableListOf<QuoteDataItem>()
            val quote = QuoteDataItem(
                quote = "I don't think meals have any business being deductible. I'm for separation of calories and corporations.",
                author = "Ralph Nader",
                category = "business"
            )
            quoteList.add(quote)
            doReturn(quoteList)
                .`when`(networkService)
                .getRandomQuote()
            repository.getRandomQuote().test {
                assertEquals(quoteList[0], awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            verify(networkService, times(1)).getRandomQuote()
        }
    }

    @Test
    fun getRandomQuote_whenNetworkServiceResponseError_shouldReturnError() {
        runTest {
            val message = "RuntimeException occurred"
            doThrow(RuntimeException(message))
                .`when`(networkService)
                .getRandomQuote()
            repository.getRandomQuote().test {
                assertEquals(RuntimeException(message).message, awaitError().message)
                cancelAndIgnoreRemainingEvents()
            }
            verify(networkService, times(1)).getRandomQuote()
        }
    }

}