package com.example.spend.ui.viewmodel.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.entry.EntryRepository
import com.example.spend.longToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val durationMillis = 1_000L

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val currencySymbol = preferencesRepository.baseCurrencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = ""
        )

    val thereAreEntries = entryRepository.areEntriesPresent()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
            initialValue = false
        )

    val transactions =
        entryRepository.getEntryIconAndColor()
            .map {
                it.groupBy { entryCategory -> longToDate(longDate = entryCategory.entry.epochSeconds) }
            }
            .flowOn(context = Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = durationMillis),
                initialValue = emptyMap()
            )
}