package com.example.spend.data.room

import android.content.Context

class DatabaseRepositoryContainer(context: Context): RepositoryContainer {
    override val databaseRepository: EntryRepository by lazy {
        DefaultRepository(EntryDatabase.getDatabase(context).entryDao())
    }
}