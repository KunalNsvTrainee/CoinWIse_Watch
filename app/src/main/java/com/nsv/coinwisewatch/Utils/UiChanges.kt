package com.nsv.coinwisewatch.Utils

import android.content.Context
import android.view.inputmethod.InputMethodManager

class UiChanges {
    fun hideKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


    }