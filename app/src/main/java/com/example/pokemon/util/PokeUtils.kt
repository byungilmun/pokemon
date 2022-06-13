package com.example.pokemonkotlin.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

class PokeUtils {
    companion object {
        fun getHighlightedText(string: String, searchString: String, color: Int): SpannableString? {
            var spannableString = SpannableString(string)
            var targetStartIndex = string.indexOf(searchString)
            var targetEndIndex = targetStartIndex + searchString.length
            if (targetStartIndex < 0 || targetEndIndex < 0) {
                return null
            }
            spannableString.setSpan(
                ForegroundColorSpan(color),
                targetStartIndex,
                targetEndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
        }
    }
}