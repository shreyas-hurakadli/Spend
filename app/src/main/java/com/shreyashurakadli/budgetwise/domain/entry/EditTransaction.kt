package com.shreyashurakadli.budgetwise.domain.entry

import androidx.room.withTransaction
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.account.Account
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.data.room.entry.Entry
import com.shreyashurakadli.budgetwise.data.room.entry.EntryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EditTransaction @Inject constructor(
    private val entryRepository: EntryRepository,
    private val accountRepository: AccountRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        entry: Entry,
        editedEntry: Entry,
        allAccount: Account
    ): Boolean = try {
        database.withTransaction {
            val oldImpact = entry.amount * if (entry.isExpense) -1.0 else 1.0
            val newImpact = editedEntry.amount * if (editedEntry.isExpense) -1.0 else 1.0

            if (entry.accountId != editedEntry.accountId) {
                val prevAccount = accountRepository.getAccountById(id = entry.accountId).first()
                val nextAccount = accountRepository.getAccountById(id = editedEntry.accountId).first()

                prevAccount?.let {
                    accountRepository.update(it.copy(balance = it.balance - oldImpact))
                }
                nextAccount?.let {
                    accountRepository.update(it.copy(balance = it.balance + newImpact))
                }
            } else {
                val curAccount = accountRepository.getAccountById(id = editedEntry.accountId).first()
                curAccount?.let {
                    accountRepository.update(it.copy(balance = it.balance - oldImpact + newImpact))
                }
            }

            val allAccountLatest = accountRepository.getFirstAccount().first()
            val isTransfer = entry.categoryId in 3L..4L
            val isNewTransfer = editedEntry.categoryId in 3L..4L
            
            val oldAllImpact = if (isTransfer) 0.0 else oldImpact
            val newAllImpact = if (isNewTransfer) 0.0 else newImpact
            
            accountRepository.update(allAccountLatest.copy(balance = allAccountLatest.balance - oldAllImpact + newAllImpact))

            entryRepository.update(entry = editedEntry)
        }
        true
    } catch (e: Exception) {
        false
    }
}