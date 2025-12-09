package com.example.spend.ui.viewmodel.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.dto.EntryCategory
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val defaultRepository: EntryRepository
): ViewModel() {
    val transactions: StateFlow<List<EntryCategory>> =
        defaultRepository.getEntryIconAndColor()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(durationMillis),
                initialValue = emptyList()
            )
}