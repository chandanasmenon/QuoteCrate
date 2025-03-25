package com.chandana.quotecrate.ui.quoteDisplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandana.quotecrate.data.model.QuoteDataItem
import com.chandana.quotecrate.data.repository.QuoteRepository
import com.chandana.quotecrate.ui.base.UiState
import com.chandana.quotecrate.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val repository: QuoteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<QuoteDataItem>>(UiState.Loading)
    val uiState: StateFlow<UiState<QuoteDataItem>> = _uiState

    fun getRandomQuote() {
        viewModelScope.launch(dispatcherProvider.main) {
            repository.getRandomQuote()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Unable to get the quote!")
                }
                .collect {
                    _uiState.value = UiState.Success(it)
                }
        }
    }
}