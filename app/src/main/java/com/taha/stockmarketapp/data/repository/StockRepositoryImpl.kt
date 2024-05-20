package com.taha.stockmarketapp.data.repository

import com.taha.stockmarketapp.data.csv.CSVParser
import com.taha.stockmarketapp.data.local.StockDatabase
import com.taha.stockmarketapp.data.mapper.toCompanyInfo
import com.taha.stockmarketapp.data.mapper.toCompanyListing
import com.taha.stockmarketapp.data.mapper.toCompanyListingEntity
import com.taha.stockmarketapp.data.remote.StockApi
import com.taha.stockmarketapp.domain.model.CompanyInfo
import com.taha.stockmarketapp.domain.model.CompanyListing
import com.taha.stockmarketapp.domain.model.IntradayInfo
import com.taha.stockmarketapp.domain.repository.StockRepository
import com.taha.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>
) : StockRepository{

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if(shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol = symbol)
            val data = intradayInfoParser.parse(response.byteStream())
            Resource.Success(data)
        } catch (exc: IOException){
            Resource.Error(
                message = exc.message ?: "Couldn't reach the server! Please check your internet connection."
            )
        } catch (exc: HttpException){
            Resource.Error(
                message = exc.message ?: "Unexpected error occurred!"
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val response = api.getCompanyInfo(symbol = symbol)
            Resource.Success(response.toCompanyInfo())
        } catch (exc: IOException){
            Resource.Error(
                message = exc.message ?: "Couldn't reach the server! Please check your internet connection."
            )
        } catch (exc: HttpException){
            Resource.Error(
                message = exc.message ?: "Unexpected error occurred!"
            )
        }
    }


}