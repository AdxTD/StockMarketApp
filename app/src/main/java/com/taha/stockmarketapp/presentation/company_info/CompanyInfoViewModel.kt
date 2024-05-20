package com.taha.stockmarketapp.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.stockmarketapp.domain.repository.StockRepository
import com.taha.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val repository: StockRepository,
    private val savedStateHandle: SavedStateHandle
) :ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(
                isLoading = true
            )
            val companyInfoRes = async { repository.getCompanyInfo(symbol) }
            val intradayInfo = async { repository.getIntradayInfo(symbol) }

            when(val result = companyInfoRes.await()){
                is Resource.Success -> {
                    state = state.copy(
                        companyInfo = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Loading -> Unit
                is Resource.Error -> {
                    state = state.copy(
                        companyInfo = null,
                        isLoading = false,
                        error = result.message
                    )
                }
            }
            when(val result = intradayInfo.await()){
                is Resource.Success -> {
                    state = state.copy(
                        stocksInfo = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Loading -> Unit
                is Resource.Error -> {
                    state = state.copy(
                        stocksInfo = emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }
}