package com.dailyapps.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Gray = Color(0xFF2b2b2b)
val Placeholder = Color(0xFFCECECE)
val Primary = Color(0xFF347AB6)
val Active = Color(0xFF0AA576)
val Finish = Color(0xFF6D6D6D)
val Primary80 = Color(0xB1C155EF)
val Primary50 = Color(0xFFF6EBFB)
val White = Color(0xFFFFFFFF)
val Neutral100 = Color(0xFFE5E5E5)
val Neutral300 = Color(0xFFACACAC)
val Neutral400 = Color(0xFF979797)
val Neutral500 = Color(0xFF828282)
val Neutral700 = Color(0xFF555555)
val Neutral900 = Color(0xFF2F2F2F)
val Black = Color(0xFF000000)
val Divider = Color(0xFFE5E5E5)
val Divider2 = Color(0xFFD9D9D9)
val BgProfile = Color(0xFFF5F5F7)
val Danger600 = Color(0xFFF42929)

val Primary500 = Color(0xFF347AB6)  // Using the same value as Primary for consistency
val Primary600 = Color(0xFF2A62A3)  // Slightly darker version of Primary500
val Success500 = Color(0xFF0AA576)  // Using the same value as Active for consistency
val Warning500 = Color(0xFFFFB800)  // New amber/yellow warning color

val Colors.Neutral_900
    @Composable
    get() = if (isSystemInDarkTheme()) White else Neutral900
