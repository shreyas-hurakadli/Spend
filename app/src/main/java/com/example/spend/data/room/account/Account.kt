package com.example.spend.data.room.account

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.spend.data.local.file.CsvExportableEntity
import com.example.spend.escapeCsv

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
): CsvExportableEntity {
    companion object {
        const val HEADER = "id,name,balance,color,icon"
    }

    override fun toCsv(): String = "$id,${name.escapeCsv()},$balance,${color.value},$icon"
}
