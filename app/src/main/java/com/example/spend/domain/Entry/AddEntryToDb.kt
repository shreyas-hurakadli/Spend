package com.example.spend.domain.Entry

import androidx.room.withTransaction
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import javax.inject.Inject

class AddEntryToDb @Inject constructor(
    private val entryRepository: EntryRepository,
    private val accountRepository: AccountRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        entry: Entry,
        fromAccount: Account,
        toAccount: Account,
        allAccount: Account,
        transferIncomeId: Long,
        selectedIndex: Int
    ): Boolean = try {
        database.withTransaction {
            entryRepository.insert(entry)
            if (selectedIndex > 0) {
                accountRepository.update(
                    account = fromAccount.copy(
                        balance = fromAccount.balance - entry.amount,
                    )
                )
                if (selectedIndex == 1) {
                    accountRepository.update(
                        account = allAccount.copy(
                            balance = allAccount.balance - entry.amount
                        )
                    )
                }
            } else {
                accountRepository.update(
                    account = fromAccount.copy(
                        balance = fromAccount.balance + entry.amount
                    )
                )
                accountRepository.update(
                    account = allAccount.copy(
                        balance = allAccount.balance + entry.amount
                    )
                )
            }

            if (toAccount != Account()) {
                accountRepository.update(
                    account = toAccount.copy(
                        balance = toAccount.balance + entry.amount
                    )
                )
                entryRepository.insert(
                    entry = entry.copy(categoryId = transferIncomeId)
                )
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}