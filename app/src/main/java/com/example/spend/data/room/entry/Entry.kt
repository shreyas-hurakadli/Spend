package com.example.spend.data.room.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.spend.data.local.file.CsvExportableEntity
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.category.Category
import com.example.spend.escapeCsv

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("account_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category_id"),
            onDelete = ForeignKey.CASCADE
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
) : CsvExportableEntity {
    companion object {
        const val HEADER = "id,amount,is_expense,epoch_seconds,category_id,account_id,description"
    }

    override fun toCsv() = "$id,$amount,$isExpense,$epochSeconds,$categoryId,$accountId,${description.escapeCsv()}"
}
