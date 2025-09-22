package com.example.spend.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.account.AccountRepository
import com.example.spend.data.room.account.DefaultAccountRepository
import com.example.spend.data.room.category.DefaultCategoryRepository
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val defaultRepository: EntryRepository,
    private val defaultAccountRepository: DefaultAccountRepository,
    private val defaultCategoryRepository: DefaultCategoryRepository
) : ViewModel() {
    var selectedIndex by mutableIntStateOf(1)
        private set

    var amount by mutableStateOf("0")
        private set

    var time by mutableStateOf(System.currentTimeMillis())
        private set

    var description by mutableStateOf("")
        private set

    fun changeTime(value: Long) {
        time = value
    }

    fun changeSelectedIndex(index: Int) {
        selectedIndex = index
    }

    fun changeDescription(text: String) {
        description = text
    }

    fun changeAmount(value: String) {
        amount = value
    }
}