package com.taha.stockmarketapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class IntradayInfoEntity(
    @PrimaryKey val id: Int? = null,
    val symbol: String,
    val date: LocalDateTime,
    val close: Double
)
