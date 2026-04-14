package com.example.spend.domain.account

import androidx.room.withTransaction
import com.example.spend.data.room.RoomDatabaseClass
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.account.AccountRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteAccount @Inject constructor(
    private val accountRepository: AccountRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(account: Account): Boolean =
        try {
            database.withTransaction {
                accountRepository.delete(account)
                val firstAccount = accountRepository.getFirstAccount().first()
                accountRepository.update(account = firstAccount.copy(balance = firstAccount.balance - account.balance))
            }
            true
        } catch (e: Exception) {
            false
        }
}