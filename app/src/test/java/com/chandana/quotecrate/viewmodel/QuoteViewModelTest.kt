package com.chandana.quotecrate.viewmodel

import app.cash.turbine.test
import com.chandana.quotecrate.data.model.QuoteDataItem
import com.chandana.quotecrate.data.repository.QuoteRepository
import com.chandana.quotecrate.ui.base.UiState
import com.chandana.quotecrate.ui.quoteDisplay.QuoteViewModel
import com.chandana.quotecrate.utils.DispatcherProvider
import com.chandana.quotecrate.utils.TestDispatcherProvider
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class QuoteViewModelTest {

    @Mock
    private lateinit var repository: QuoteRepository

    private lateinit var viewModel: QuoteViewModel

    private lateinit var dispatcherProvider: DispatcherProvider

    @Before
    fun setUp() {
        dispatcherProvider = TestDispatcherProvider()
        viewModel = QuoteViewModel(
            repository = repository,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun getRandomQuote_whenRepositoryResponseSuccess_shouldSetSuccessUiState() {
        runTest {
            val quote = QuoteDataItem(
                quote = "I don't think meals have any business being deductible. I'm for separation of calories and corporations.",
                author = "Ralph Nader",
                category = "business"
            )
            doReturn(flowOf(quote))
                .`when`(repository)
                .getRandomQuote()
            viewModel.getRandomQuote()
            viewModel.uiState.test {
                assertEquals(UiState.Success(quote), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            verify(repository, times(1)).getRandomQuote()
        }
    }

    @Test
    fun getRandomQuote_whenRepositoryResponseError_shouldSetErrorUiState() {
        runTest {
            val message = "IllegalStateException error occurred"
            doReturn(flow<QuoteDataItem> {
                throw IllegalStateException(message)
            }).`when`(repository)
                .getRandomQuote()
            viewModel.getRandomQuote()
            viewModel.uiState.test {
                assertEquals(
                    UiState.Error(IllegalStateException(message).message.toString()),
                    awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
            verify(repository, times(1)).getRandomQuote()
        }
    }

}