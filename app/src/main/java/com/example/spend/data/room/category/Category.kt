package com.example.spend.data.room.category

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.spend.data.local.file.CsvExportableEntity

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
    val color: Color = Color(0xFF77DD77),
    val icon: String? = null
): CsvExportableEntity {
    companion object {
        const val HEADER = "id,name,is_expense,color,icon"
    }

    override fun toCsv(): String = "$id,$name,$isExpense,${color.value},$icon"
}
