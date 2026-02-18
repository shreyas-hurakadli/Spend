package com.example.spend.data.room.currency

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "currencies",
    indices = [Index(value = ["name"], unique = true)]
)
data class Currency(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    val rate: Double = 0.00
)