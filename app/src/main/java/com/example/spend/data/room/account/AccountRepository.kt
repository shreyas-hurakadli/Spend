package com.example.spend.data.room.account

import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun insert(account: Account): Long

    suspend fun update(account: Account)

    suspend fun delete(account: Account)

    fun getAccountById(id: Long): Flow<Account?>

    fun getFirstAccount(): Flow<Account>
    fun getAllAccounts(): Flow<List<Account>>
}