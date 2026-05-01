package com.shreyashurakadli.budgetwise.data.dto

import androidx.compose.ui.graphics.Color
import androidx.room.Embedded
import com.shreyashurakadli.budgetwise.data.room.entry.Entry

data class EntryCategory(
    @Embedded
    val entry: Entry,
    val name: String,
    val icon: String?,
    val color: Color
)