package com.dailyapps.common.utils

import android.text.Html
import java.util.Locale

fun String.uppercaseFirstChar() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

val String.Unauthorized
    get() = this.equals("Authentication Failed", true)

fun emptyString(): String = ""

fun String.convertHtmlToString(): String {
    val htmlText = Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    return htmlText.trim().toString()
}

fun String.formatToken(): String {
    return "Bearer $this"
}

fun String.httpFormat() = "http://$this"
