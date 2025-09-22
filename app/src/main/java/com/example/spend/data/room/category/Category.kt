package com.example.spend.data.room.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name", "is_expense"], unique = true)]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    @ColumnInfo(name = "is_expense")
    val isExpense: Boolean = true,
    val color: Int = 0,
    val icon: String? = ""
)
