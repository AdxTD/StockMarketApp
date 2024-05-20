package com.taha.stockmarketapp.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.opencsv.CSVReader
import com.taha.stockmarketapp.data.mapper.toIntradayInfo
import com.taha.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.taha.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor(): CSVParser<IntradayInfo>{

    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        val dayOfMonth = when (LocalDate.now().dayOfWeek) {
            DayOfWeek.MONDAY -> LocalDate.now().minusDays(3).dayOfMonth
            DayOfWeek.SUNDAY -> LocalDate.now().minusDays(2).dayOfMonth
            else -> LocalDate.now().minusDays(1).dayOfMonth
        }
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val date = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    IntradayInfoDto(
                        timestamp = date,
                        close = close.toDouble()
                    ).toIntradayInfo()
                }
                .filter {
                    it.date.dayOfMonth == dayOfMonth
                }
                .sortedBy {
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }

}