package com.shreyashurakadli.budgetwise.domain.account

import androidx.room.withTransaction
import com.shreyashurakadli.budgetwise.data.room.RoomDatabaseClass
import com.shreyashurakadli.budgetwise.data.room.account.Account
import com.shreyashurakadli.budgetwise.data.room.account.AccountRepository
import com.shreyashurakadli.budgetwise.ui.data.MAX_ACCOUNT_NAME_LENGTH
import javax.inject.Inject

class EditAccount @Inject constructor(
    private val accountRepository: AccountRepository,
    private val database: RoomDatabaseClass
) {
    suspend operator fun invoke(
        account: Account,
        editedAccount: Account,
    ): Boolean = try {
        if (!validateEditedAccount(editedAccount = editedAccount)) {
            throw IllegalStateException()
        }
        database.withTransaction {
            if (account != editedAccount) {
                accountRepository.update(account = editedAccount)
            }
        }
        true
    } catch (e: Exception) {
        false
    }

    private fun validateEditedAccount(
        editedAccount: Account,
    ): Boolean = editedAccount.name.length <= MAX_ACCOUNT_NAME_LENGTH
}