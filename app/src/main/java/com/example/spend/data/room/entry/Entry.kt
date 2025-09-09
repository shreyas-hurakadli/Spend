package com.example.spend.data.room.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.spend.data.room.account.Account

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("account_id")
        )
    ]
)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double = 0.00,
    val isExpense: Boolean = true,
    val epochSeconds: Long = 0L,
    val category: String = "",
    @ColumnInfo(name = "account_id")
    val accountId: Long = 0,
    val description: String = "",
)
