package com.example.proyek_android.Classes

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*


class MoneyTextWatcher(var editText: EditText) : TextWatcher {
    private val locale: Locale = Locale("id", "ID")
    private val formatter: DecimalFormat = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    private var editTextWeakReference: WeakReference<EditText>? = null

    init {
        editTextWeakReference = WeakReference(editText)
        formatter.maximumFractionDigits = 0
        formatter.roundingMode = RoundingMode.FLOOR

        val symbol = DecimalFormatSymbols(locale)
        symbol.setCurrencySymbol(symbol.getCurrencySymbol().toString() + " ")
        formatter.decimalFormatSymbols = symbol
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        val editText = editTextWeakReference!!.get()
        if (editText == null || editText.text.toString().isEmpty()) {
            return
        }
        editText.removeTextChangedListener(this)

        val parsed: BigDecimal = parseCurrencyValue(editText.text.toString())
        val formatted: String = formatter.format(parsed).substring(3)

        editText.setText(formatted)
        editText.setSelection(formatted.length)
        editText.addTextChangedListener(this)

//        Log.d("TAG WATCHER", "afterTextChanged: sampe sini $formatted")
    }

    fun parseCurrencyValue(value: String): BigDecimal {
        try {
            val replaceRegex = java.lang.String.format(
                "[%s,.\\s]",
                Objects.requireNonNull(formatter.currency).getSymbol(locale)
            )
            val currencyValue = value.replace(replaceRegex.toRegex(), "")
            return BigDecimal(currencyValue)
        } catch (e: Exception) {
            Log.e("App", e.message, e)
        }
        return BigDecimal.ZERO
    }
}