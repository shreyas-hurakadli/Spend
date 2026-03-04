package com.example.spend.data.local.file

import android.net.Uri

interface CsvExportableRepository {
    suspend fun writeFile(
        parentDirectory: Uri,
        fileName: String,
        header: String,
        data: List<CsvExportableEntity>
    )
}