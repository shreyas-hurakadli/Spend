package com.example.spend.ui.viewmodel.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.data.datastore.config.PreferencesRepository
import com.example.spend.data.room.currency.CurrencyRepository
import com.example.spend.data.workmanager.currency.CurrencyApiRepository
import com.example.spend.toTwoDecimal
import com.example.spend.ui.CurrencyIcon
import com.example.spend.ui.currencyIcons
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.RoundingMode
import javax.inject.Inject

private const val DURATION_MILLIS = 1_000L

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val defaultCurrencyRepository: CurrencyRepository,
    private val defaultCurrencyApiRepository: CurrencyApiRepository,
    private val defaultPreferencesRepository: PreferencesRepository
) : ViewModel() {
    val currencies = defaultCurrencyRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(value = true)
    val isLoading = _isLoading.asStateFlow()

    private val _showBaseCurrencyBottomSheet = MutableStateFlow(value = false)
    val showBaseCurrencyBottomSheet = _showBaseCurrencyBottomSheet.asStateFlow()

    private val _showQuoteCurrencyBottomSheet = MutableStateFlow(value = false)
    val showQuoteCurrencyBottomSheet = _showQuoteCurrencyBottomSheet.asStateFlow()

    private val _baseCurrency = MutableStateFlow(value = currencyIcons[0])
    val baseCurrency = _baseCurrency.asStateFlow()

    private val _quoteCurrency = MutableStateFlow(value = currencyIcons[28])
    val quoteCurrency = _quoteCurrency.asStateFlow()

    private val _baseCurrencyValue = MutableStateFlow(value = "0")
    val baseCurrencyValue = _baseCurrencyValue.asStateFlow()

    val conversionValue = combine(
        currencies,
        _quoteCurrency,
        _baseCurrency
    ) { currencyList, quoteCurr, baseCurr ->
        val quoteCurrencyRate =
            currencyList.find { it.name == quoteCurr.code }?.rate ?: 1.00
        val baseCurrencyRate =
            currencyList.find { it.name == baseCurr.code }?.rate ?: 1.00
        (quoteCurrencyRate / baseCurrencyRate)
            .toBigDecimal()
            .setScale(5, RoundingMode.HALF_UP)
            .toDouble()
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = 1.00
        )

    val quoteCurrencyValue = combine(
        conversionValue,
        _baseCurrencyValue
    ) { conversionVal, baseCurrVal ->
        (conversionVal * (if (baseCurrVal.isEmpty() || baseCurrVal == ".") 1.00 else baseCurrVal.toDouble())).toTwoDecimal()
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = DURATION_MILLIS),
            initialValue = 1.00
        )

    fun updateBaseCurrency(currencyIcon: CurrencyIcon) {
        _baseCurrency.value = currencyIcon
    }

    fun updateQuoteCurrency(currencyIcon: CurrencyIcon) {
        _quoteCurrency.value = currencyIcon
    }

    fun toggleShowBaseCurrencyBottomSheet() {
        _showBaseCurrencyBottomSheet.value = !(_showBaseCurrencyBottomSheet.value)
    }

    fun toggleShowQuoteCurrencyBottomSheet() {
        _showQuoteCurrencyBottomSheet.value = !(_showQuoteCurrencyBottomSheet.value)
    }

    fun swapCurrencies() {
        val temp = _quoteCurrency.value
        _quoteCurrency.value = _baseCurrency.value
        _baseCurrency.value = temp
    }

    fun updateInputValue(value: String) {
        if (value == ".") {
            if (_baseCurrencyValue.value.count { it == '.' } > 0) {
                return
            }
        } else if (value == "B") {
            if (_baseCurrencyValue.value != "0") {
                _baseCurrencyValue.value = _baseCurrencyValue.value.dropLast(n = 1)
                if (_baseCurrencyValue.value.isEmpty()) {
                    _baseCurrencyValue.value = "0"
                }
            }
            return
        } else if (value == "0") {
            if (_baseCurrencyValue.value == "0") {
                return
            }
        } else {
            if (_baseCurrencyValue.value.substringAfter(
                    delimiter = '.',
                    missingDelimiterValue = ""
                ).length >= 2
            ) {
                return
            }
        }
        if (_baseCurrencyValue.value == "0") {
            if (value != ".") {
                _baseCurrencyValue.value = ""
            }
        }
        _baseCurrencyValue.value += value
    }

    init {
        if (currencies.value.isEmpty()) {
            getExchangeData()
        }
        viewModelScope.launch {
            _baseCurrency.value =
                currencyIcons.find { it.code == defaultPreferencesRepository.baseCurrency.first() }
                    ?: currencyIcons[0]
            _isLoading.value = false
        }
    }

    private fun getExchangeData() {
        defaultCurrencyApiRepository.getExchangeRateNow()
    }
}