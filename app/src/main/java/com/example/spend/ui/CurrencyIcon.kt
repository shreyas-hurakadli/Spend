package com.example.spend.ui

import com.example.spend.R

data class CurrencyIcon(
    val name: String,
    val code: String,
    val flag: Int,
    val symbol: String
)

val currencyIcons: List<CurrencyIcon> = listOf(
    CurrencyIcon(
        name = "Australian Dollar",
        code = "AUD",
        flag = R.drawable.australia,
        symbol = "A$"
    ),
    CurrencyIcon(
        name = "Brazilian Real",
        code = "BRL",
        flag = R.drawable.brazil,
        symbol = "R$"
    ),
    CurrencyIcon(
        name = "Canadian Dollar",
        code = "CAD",
        flag = R.drawable.canada,
        symbol = "C$"
    ),
    CurrencyIcon(
        name = "Swiss Franc",
        code = "CHF",
        flag = R.drawable.switzerland,
        symbol = "Fr"
    ),
    CurrencyIcon(
        name = "Chinese Yuan",
        code = "CNY",
        flag = R.drawable.china,
        symbol = "¥"
    ),
    CurrencyIcon(
        name = "Czech Koruna",
        code = "CZK",
        flag = R.drawable.czech_republic,
        symbol = "Kč"
    ),
    CurrencyIcon(
        name = "Danish Krone",
        code = "DKK",
        flag = R.drawable.denmark,
        symbol = "kr"
    ),
    CurrencyIcon(
        name = "Euro",
        code = "EUR",
        flag = R.drawable.europe,
        symbol = "€"
    ),
    CurrencyIcon(
        name = "Pound Sterling",
        code = "GBP",
        flag = R.drawable.great_britain,
        symbol = "£"
    ),
    CurrencyIcon(
        name = "Hong Kong Dollar",
        code = "HKD",
        flag = R.drawable.hong_kong,
        symbol = "HK$"
    ),
    CurrencyIcon(
        name = "Hungarian Forint",
        code = "HUF",
        flag = R.drawable.hungary,
        symbol = "Ft"
    ),
    CurrencyIcon(
        name = "Indonesian Rupiah",
        code = "IDR",
        flag = R.drawable.indonesia,
        symbol = "Rp"
    ),
    CurrencyIcon(
        name = "Israeli New Shekel",
        code = "ILS",
        flag = R.drawable.israel,
        symbol = "₪"
    ),
    CurrencyIcon(
        name = "Indian Rupee",
        code = "INR",
        flag = R.drawable.india,
        symbol = "₹"
    ),
    CurrencyIcon(
        name = "Icelandic Króna",
        code = "ISK",
        flag = R.drawable.iceland,
        symbol = "kr"
    ),
    CurrencyIcon(
        name = "Japanese Yen",
        code = "JPY",
        flag = R.drawable.japan,
        symbol = "¥"
    ),
    CurrencyIcon(
        name = "South Korean Won",
        code = "KRW",
        flag = R.drawable.south_korea,
        symbol = "₩"
    ),
    CurrencyIcon(
        name = "Mexican Peso",
        code = "MXN",
        flag = R.drawable.mexico,
        symbol = "$"
    ),
    CurrencyIcon(
        name = "Malaysian Ringgit",
        code = "MYR",
        flag = R.drawable.malaysia,
        symbol = "RM"
    ),
    CurrencyIcon(
        name = "Norwegian Krone",
        code = "NOK",
        flag = R.drawable.norway,
        symbol = "kr"
    ),
    CurrencyIcon(
        name = "New Zealand Dollar",
        code = "NZD",
        flag = R.drawable.new_zealand,
        symbol = "NZ$"
    ),
    CurrencyIcon(
        name = "Philippine Peso",
        code = "PHP",
        flag = R.drawable.philippines,
        symbol = "₱"
    ),
    CurrencyIcon(
        name = "Polish Złoty",
        code = "PLN",
        flag = R.drawable.poland,
        symbol = "zł"
    ),
    CurrencyIcon(
        name = "Romanian Leu",
        code = "RON",
        flag = R.drawable.romania,
        symbol = "lei"
    ),
    CurrencyIcon(
        name = "Swedish Krona",
        code = "SEK",
        flag = R.drawable.sweden,
        symbol = "kr"
    ),
    CurrencyIcon(
        name = "Singapore Dollar",
        code = "SGD",
        flag = R.drawable.singapore,
        symbol = "S$"
    ),
    CurrencyIcon(
        name = "Thai Baht",
        code = "THB",
        flag = R.drawable.thailand,
        symbol = "฿"
    ),
    CurrencyIcon(
        name = "Turkish Lira",
        code = "TRY",
        flag = R.drawable.turkey,
        symbol = "₺"
    ),
    CurrencyIcon(
        name = "US Dollar",
        code = "USD",
        flag = R.drawable.united_states,
        symbol = "$"
    ),
    CurrencyIcon(
        name = "South African Rand",
        code = "ZAR",
        flag = R.drawable.south_africa,
        symbol = "R"
    ),
)
