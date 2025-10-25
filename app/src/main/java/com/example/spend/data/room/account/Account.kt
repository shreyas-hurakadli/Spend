package com.example.spend.data.room.account

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    indices = [Index(value = ["name"], unique = true)]
)
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val balance: Double = 0.00,
    val color: Color = Color(0xFF77DD77),
    val icon: String? = ""
)
