package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val defaultRepository: EntryRepository
): ViewModel() {
    val transactions: StateFlow<List<Entry>> =
        defaultRepository.getAllEntries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = emptyList()
            )
}