package com.dailyapps.common.components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyapps.common.Neutral100
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Neutral500
import com.dailyapps.common.R

enum class ButtonIconType {
    DATE,
    FILTER
}

@Composable
fun DateRangeButton(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    onClick: () -> Unit,
    iconType: ButtonIconType = ButtonIconType.DATE,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Neutral500
        ),
        border = BorderStroke(1.dp, if (isError) Color.Red else Neutral100),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseText(
                text = text.ifEmpty { label },
                fontColor = if (text.isNotEmpty()) {
                    if (isError) Color.Red else Neutral500
                } else {
                    if (isError) Color.Red else Neutral300
                },
                fontSize = 14.sp,
                fontFamily = FontType.LIGHT
            )

            when (iconType) {
                ButtonIconType.DATE -> {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Date Icon",
                        tint = if (isError) Color.Red else Neutral300
                    )
                }
                ButtonIconType.FILTER -> {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter),
                        contentDescription = "Filter Icon",
                        tint = if (isError) Color.Red else Neutral300
                    )
                }
            }
        }
    }
}

// make preview
@Preview(showBackground = true)
@Composable
fun DateRangeButtonPreview() {
    DateRangeButton(
        text = "Select Date",
        label = "Select Date",
        onClick = {}
    )
}
