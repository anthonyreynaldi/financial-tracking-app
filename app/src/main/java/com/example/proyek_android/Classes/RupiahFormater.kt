package com.example.proyek_android.Classes

import java.lang.ref.WeakReference
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

class RupiahFormater {
    private val locale: Locale = Locale("id", "ID")
    private val formatter: DecimalFormat = NumberFormat.getCurrencyInstance(locale) as DecimalFormat

    init {
        formatter.maximumFractionDigits = 0
        formatter.roundingMode = RoundingMode.FLOOR

        val symbol = DecimalFormatSymbols(locale)
        symbol.setCurrencySymbol(symbol.getCurrencySymbol().toString() + " ")
        formatter.decimalFormatSymbols = symbol
    }

    fun format(nominal : Int): String? {
        return formatter.format(nominal)
    }
}