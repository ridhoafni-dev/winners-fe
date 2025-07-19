package com.dailyapps.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dailyapps.common.components.BaseText

@Composable
fun EmptyList(
    message: String = stringResource(R.string.empty_data)
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseText(
            modifier = Modifier.align(Alignment.Center), text = message
        )
    }
}