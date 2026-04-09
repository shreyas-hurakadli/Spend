package com.example.spend.data.room.account

import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun insert(account: Account)

    suspend fun update(account: Account)

    suspend fun delete(account: Account)

    suspend fun deleteAll()

    suspend fun resetData()

    fun thereAreAccounts(): Flow<Boolean>

    fun getAccountById(id: Long): Flow<Account?>

    fun getFirstAccount(): Flow<Account>
    fun getAllAccounts(): Flow<List<Account>>
}