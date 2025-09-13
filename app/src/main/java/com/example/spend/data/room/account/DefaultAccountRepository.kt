package com.example.spend.data.room.account

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class DefaultAccountRepository @Inject constructor(private val dao: AccountDao) :
    AccountRepository {
    override suspend fun insert(account: Account) = dao.insert(account)

    override suspend fun update(account: Account) = dao.update(account)

    override suspend fun delete(account: Account) = dao.delete(account)

    override fun getAccountById(id: Long) = dao.getAccountById(id)

    override fun getFirstAccount() = dao.getFirstAccount()

    override fun getAllAccounts(): Flow<List<Account>> = dao.getAllAccounts()
}