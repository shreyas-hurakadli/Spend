package com.example.spend.data.local.file

interface CsvExportableEntity {
    fun toCsv(): String
}