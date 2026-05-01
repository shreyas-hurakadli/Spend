package com.shreyashurakadli.budgetwise.data.local.file

interface CsvExportableEntity {
    fun toCsv(): String
}