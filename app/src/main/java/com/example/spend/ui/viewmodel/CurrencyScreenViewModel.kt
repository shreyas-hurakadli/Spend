package com.example.spend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CurrencyScreenViewModel @Inject constructor(
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _selectedCode: MutableStateFlow<String?> = MutableStateFlow(value = null)
    val selectedCode = _selectedCode.asStateFlow()

    private val _confirmed: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val confirmed = _confirmed.asStateFlow()

    fun selectCurrency(codeSymbol: String) {
        _selectedCode.value = codeSymbol
    }

    fun confirmSelection() {
        _selectedCode.value?.let {
            viewModelScope.launch {
                withContext(context = Dispatchers.IO) {
                    defaultPreferencesRepository.registerBaseCurrency(baseCurrency = it)
                    _confirmed.value = true
                }
            }
        }
    }
}