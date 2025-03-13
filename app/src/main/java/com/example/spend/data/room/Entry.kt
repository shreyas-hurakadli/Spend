package com.example.spend.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String = "",
    val tag: String = "",
    val bill: Int = 0,
    val date: Long = 0L
)
