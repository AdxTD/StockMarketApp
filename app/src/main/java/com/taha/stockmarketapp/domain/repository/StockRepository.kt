package com.taha.stockmarketapp.domain.repository

import com.taha.stockmarketapp.domain.model.CompanyListing
import com.taha.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>


}