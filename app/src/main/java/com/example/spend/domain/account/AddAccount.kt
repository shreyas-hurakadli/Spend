package com.example.spend.domain.account

import androidx.room.withTransaction
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import javax.inject.Inject

class AddAccount @Inject constructor(
    private val accountRepository: AccountRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        account: Account,
        allAccount: Account,
        balance: Double
    ): Boolean = try {
        if (allAccount == Account()) {
            throw IllegalStateException()
        }
        database.withTransaction {
            accountRepository.insert(account = account)
            accountRepository.update(
                account = allAccount.copy(
                    balance = balance + allAccount.balance
                )
            )

        }
        true
    } catch (e: Exception) {
        false
    }
}