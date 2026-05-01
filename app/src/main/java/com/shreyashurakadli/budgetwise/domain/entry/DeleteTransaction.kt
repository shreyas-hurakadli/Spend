package com.shreyashurakadli.budgetwise.domain.entry

import androidx.room.withTransaction
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.entry.Entry
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DeleteTransaction @Inject constructor(
    private val entryRepository: EntryRepository,
    private val accountRepository: AccountRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        entry: Entry,
    ): Boolean =
        try {
            database.withTransaction {
                val account = accountRepository.getAccountById(id = entry.accountId).firstOrNull()
                val firstAccount = accountRepository.getFirstAccount().firstOrNull()
                account?.let { acct ->
                    accountRepository.update(acct.copy(balance = account.balance + (entry.amount * if (entry.isExpense) 1 else -1)))
                    if (firstAccount != null) {
                        accountRepository.update(account = firstAccount.copy(balance = firstAccount.balance + (entry.amount * if (entry.isExpense) 1 else -1)))
                    }
                    entryRepository.delete(entry)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
}