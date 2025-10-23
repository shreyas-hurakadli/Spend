package com.example.spend.data.room.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.category.Category

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("account_id")
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category_id")
        )
    ],
    indices = [
        Index(value = ["account_id"]),
        Index(value = ["category_id"]),
    ]
)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amount: Double = 0.00,
    @ColumnInfo(name = "is_expense")
    val isExpense: Boolean = true,
    val epochSeconds: Long = 0L,
    @ColumnInfo(name = "category_id")
    val categoryId: Long = 0L,
    @ColumnInfo(name = "account_id")
    val accountId: Long = 0,
    val description: String = "",
)
