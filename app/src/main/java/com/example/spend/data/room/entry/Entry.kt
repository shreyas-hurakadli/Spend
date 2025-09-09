package com.example.spend.data.room.entry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double = 0.00,
    val isExpense: Boolean = true,
    val epochSeconds: Long = 0L,
    val category: String = "",
    val description: String = "",
)
