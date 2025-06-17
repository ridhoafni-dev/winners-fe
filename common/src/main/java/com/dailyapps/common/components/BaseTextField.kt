package com.dailyapps.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyapps.common.Gray
import com.dailyapps.common.Neutral400
import com.dailyapps.common.Neutral900
import com.dailyapps.common.Placeholder
import com.dailyapps.common.Primary
import com.dailyapps.common.fontLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String="",
    enable: Boolean = true,
    isError: Boolean = false,
    errorMessage: String = "",
    isDate: Boolean = false,
    onIconClick: () -> Unit = {},
    onValueChange: (String) -> Unit,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        if (title.isNotEmpty()) Text(
            text = title,
            fontFamily = fontLight,
            fontSize = 16.sp,
            color = Neutral900,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Primary,
                unfocusedIndicatorColor = Gray,
                //textColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            placeholder = {
                Text(text = placeholder, fontFamily = fontLight, fontSize = 14.sp, color = Neutral400)
            },
            isError = isError,
            onValueChange = { onValueChange(it) },
            enabled = enable,
            textStyle = TextStyle.Default.copy(fontFamily = fontLight, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(
                keyboardType = when (keyboardType)
                {
                    KeyboardType.Number -> KeyboardType.Number
                    KeyboardType.Email -> KeyboardType.Email
                    KeyboardType.Password -> KeyboardType.Password
                    else -> KeyboardType.Text
                }
            ),
            visualTransformation = if (keyboardType == KeyboardType.Password) if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
            else VisualTransformation.None,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                if (keyboardType == KeyboardType.Password){
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(imageVector = image, contentDescription = null, tint = Placeholder)
                    }
                }
                if (isDate) {
                    Icon(
                        modifier = Modifier.clickable {
                            onIconClick()
                        },
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date"
                    )
                }
            },
        )

        if (isError) Text(
            text = errorMessage,
            fontFamily = fontLight,
            fontSize = 12.sp,
            color = Color.Red,
            fontWeight = FontWeight.Light
        )

    }
}


@Preview
@Composable
fun TextFieldGeneralPreview() {
    BaseTextField(value="Password", onValueChange = {}, placeholder = "Masukan Nisn", title = "NISN", keyboardType = KeyboardType.Text, isDate = false, onIconClick = {})
}