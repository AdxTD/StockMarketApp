package com.taha.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taha.stockmarketapp.domain.model.IntradayInfo

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyInfo(companyInfoEntity: CompanyInfoEntity)

    @Query("DELETE FROM companyinfoentity WHERE UPPER(:symbol) == symbol")
    suspend fun deleteCompanyInfo(symbol: String)

    @Query("SELECT * FROM companyinfoentity WHERE UPPER(:symbol) == symbol")
    suspend fun getCompanyInfo(symbol: String): List<CompanyInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntradayInfo(intradayInfoEntity: IntradayInfoEntity)

    @Query("DELETE FROM intradayinfoentity WHERE UPPER(:symbol) == symbol")
    suspend fun deleteIntradayInfo(symbol: String)

    @Query("SELECT * FROM intradayinfoentity WHERE UPPER(:symbol) == symbol")
    suspend fun getIntradayInfo(symbol: String): List<IntradayInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    )

    @Query("DELETE FROM companylistingentity")
    suspend fun clearCompanyListings()

    @Query(
        """
            SELECT * 
            FROM companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
                UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListing(query: String): List<CompanyListingEntity>
}