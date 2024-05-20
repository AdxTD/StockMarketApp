package com.taha.stockmarketapp.di

import com.taha.stockmarketapp.data.csv.CSVParser
import com.taha.stockmarketapp.data.csv.CompanyListingsParser
import com.taha.stockmarketapp.data.csv.IntradayInfoParser
import com.taha.stockmarketapp.data.repository.StockRepositoryImpl
import com.taha.stockmarketapp.domain.model.CompanyListing
import com.taha.stockmarketapp.domain.model.IntradayInfo
import com.taha.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}