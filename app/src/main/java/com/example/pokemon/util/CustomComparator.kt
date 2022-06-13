package com.example.pokemonkotlin.util

import java.lang.StringBuilder
import java.util.*
import java.util.regex.Pattern

class CustomComparator : Comparator<String> {
    override fun compare(o1: String, o2: String): Int {
        val sb1 = StringBuilder()
        val sb2 = StringBuilder()
        for (c in o1.toCharArray()) {
            reverseLanguageOrder(sb1, c)
        }
        for (c in o2.toCharArray()) {
            reverseLanguageOrder(sb2, c)
        }
        return sb1.toString().compareTo(sb2.toString())
    }

    private fun reverseLanguageOrder(sb: StringBuilder, c: Char) {
        if (Pattern.matches("[가-힣]", StringBuilder(c.toInt()))) {
            sb.append((c.toInt() - 44030).toChar())
        } else {
            sb.append((c.toInt() + 44030).toChar())
        }
    }
}