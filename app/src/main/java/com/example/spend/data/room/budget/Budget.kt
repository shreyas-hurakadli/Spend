package com.example.spend.data.room.budget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.category.Category

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["account_id"]),
        Index(value = ["category_id"]),
    ],
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
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    @ColumnInfo(name = "start_time_stamp")
    val startTimeStamp: Long = 0L,
    val period: Long = 0L,
    @ColumnInfo(name = "account_id")
    val accountId: Long = 0L,
    @ColumnInfo(name = "category_id")
    val categoryId: Long = 0L,
)
