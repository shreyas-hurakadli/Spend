package com.example.spend.domain

import androidx.room.withTransaction
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
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
            val change = editedEntry.amount - entry.amount
            val allAccount = accountRepository.getFirstAccount().first()

            if (entry.accountId != editedEntry.accountId) {
                val editedAccount =
                    accountRepository.getAccountById(id = editedEntry.accountId)
                        .first()
                val prevAccount =
                    accountRepository.getAccountById(
                        id = entry.accountId
                    )
                        .first()

                if (change != 0.00) {
                    editedAccount?.let {
                        accountRepository.update(
                            account = it.copy(balance = it.balance + editedEntry.amount)
                        )
                    }
                } else {
                    editedAccount?.let {
                        accountRepository.update(
                            account = it.copy(
                                balance = it.balance + entry.amount
                            )
                        )
                    }
                }

                prevAccount?.let {
                    accountRepository.update(
                        account = it.copy(
                            balance = it.balance - entry.amount
                        )
                    )
                }
            } else {
                if (change != 0.00) {
                    val curAccount =
                        accountRepository.getAccountById(id = editedEntry.accountId)
                            .first()
                    curAccount?.let {
                        accountRepository.update(
                            account = it.copy(balance = it.balance + change)
                        )
                    }
                }
            }

            if (change != 0.00) {
                accountRepository.update(
                    account = allAccount.copy(balance = allAccount.balance + change)
                )
            }

            entryRepository.update(entry = editedEntry)
        }
        true
    } catch (e: Exception) {
        false
    }
}