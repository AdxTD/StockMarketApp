package com.taha.stockmarketapp.presentation.company_info

import com.taha.stockmarketapp.domain.model.CompanyInfo
import com.taha.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val stocksInfo : List<IntradayInfo> = emptyList(),
    val companyInfo: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
