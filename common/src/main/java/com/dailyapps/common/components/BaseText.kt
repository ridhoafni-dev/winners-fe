package com.dailyapps.common.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.dailyapps.common.fontLight
import com.dailyapps.common.fontMedium
import com.dailyapps.common.fontRegular
import com.dailyapps.common.fontSemiBold

@Composable
fun BaseText(
    modifier: Modifier = Modifier,
    text: String = "",
    fontFamily: FontType = FontType.REGULAR,
    fontSize: TextUnit = 14.sp,
    fontColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    fontWeight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = fontSize,
        color = fontColor,
        fontWeight = fontWeight,
        fontFamily = when(fontFamily) {
            FontType.MEDIUM -> fontMedium
            FontType.LIGHT -> fontLight
            FontType.SEMI_BOLD -> fontSemiBold
            else -> fontRegular
        },
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

enum class FontType {
    LIGHT, REGULAR, MEDIUM, SEMI_BOLD
}