package com.example.spend.data.local.file

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class DefaultCsvExportableRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CsvExportableRepository {
    private fun createFile(parentDirectory: Uri, name: String): Uri? {
        val documentUri = DocumentsContract.buildDocumentUriUsingTree(
            parentDirectory,
            DocumentsContract.getTreeDocumentId(parentDirectory)
        )
        return DocumentsContract.createDocument(
            context.contentResolver,
            documentUri,
            "text/csv",
            name
        )
    }

    override suspend fun writeFile(
        parentDirectory: Uri,
        fileName: String,
        header: String,
        data: List<CsvExportableEntity>
    ) {
        withContext(context = dispatcher) {
            val file = createFile(
                parentDirectory = parentDirectory,
                name = fileName,
            ) ?: throw IOException("Failed to create $fileName")

            context.contentResolver.openOutputStream(file)
                ?.bufferedWriter()
                ?.use { writer ->
                    writer.write(header)
                    writer.newLine()
                    data.forEach { row ->
                        writer.write(row.toCsv())
                        writer.newLine()
                    }
                }
                ?: throw IOException("Failed to write $fileName")
        }
    }
}