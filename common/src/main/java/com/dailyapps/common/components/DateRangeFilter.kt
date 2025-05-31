package com.dailyapps.common.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dailyapps.common.Neutral700
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DateRangeFilter(
    modifier: Modifier = Modifier,
    startDate: String,
    endDate: String,
    label: String = "Date Range",
    onDateRangeSelected: (startDate: String, endDate: String) -> Unit
) {
    val showDatePicker = remember { mutableStateOf(false) }

    // Format to display the selected date range
    val displayText = if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
        "$startDate to $endDate"
    } else {
        "Select date range"
    }

    // Use the existing TextFieldDropdown for styling consistency
    DateRangeButton(
        modifier = modifier,
        text = displayText,
        label = label,
        onClick = {
            showDatePicker.value = true
        },
        iconType = ButtonIconType.FILTER
    )

    // Date Range Dialog
    if (showDatePicker.value) {
        DateRangePickerDialog(
            initialStartDate = startDate,
            initialEndDate = endDate,
            onDismiss = { showDatePicker.value = false },
            onConfirm = { newStartDate, newEndDate ->
                onDateRangeSelected(newStartDate, newEndDate)
                showDatePicker.value = false
            }
        )
    }
}

@Composable
fun DateRangePickerDialog(
    initialStartDate: String,
    initialEndDate: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val maxWidth = (configuration.screenWidthDp * 0.9).dp

    // Parse initial dates if available
    val startCalendar = Calendar.getInstance()
    val endCalendar = Calendar.getInstance()

    if (initialStartDate.isNotEmpty()) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            startCalendar.time = dateFormat.parse(initialStartDate) ?: Date()
        } catch (e: Exception) {
            // Use current date if parsing fails
        }
    }

    if (initialEndDate.isNotEmpty()) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            endCalendar.time = dateFormat.parse(initialEndDate) ?: Date()
        } catch (e: Exception) {
            // Use current date if parsing fails
        }
    }

    val startDateState = remember { mutableStateOf(startCalendar) }
    val endDateState = remember { mutableStateOf(endCalendar) }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Keep track of current display text
    val currentStartDate = remember { mutableStateOf(dateFormat.format(startDateState.value.time)) }
    val currentEndDate = remember { mutableStateOf(dateFormat.format(endDateState.value.time)) }

    // Custom top-positioned dialog instead of AlertDialog
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .widthIn(max = maxWidth),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title and close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Date Range",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        // Close icon button
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Neutral700,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { onDismiss() }
                        )
                    }
                    
                    // Dialog content
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Start Date Button
                        DateRangeButton(
                            text = currentStartDate.value,
                            label = "Start Date",
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        startDateState.value.set(year, month, day)
                                        // Update display text immediately
                                        currentStartDate.value = dateFormat.format(startDateState.value.time)
                                    },
                                    startDateState.value.get(Calendar.YEAR),
                                    startDateState.value.get(Calendar.MONTH),
                                    startDateState.value.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )

                        // End Date Button
                        DateRangeButton(
                            text = currentEndDate.value,
                            label = "End Date",
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        endDateState.value.set(year, month, day)
                                        // Update display text immediately
                                        currentEndDate.value = dateFormat.format(endDateState.value.time)
                                    },
                                    endDateState.value.get(Calendar.YEAR),
                                    endDateState.value.get(Calendar.MONTH),
                                    endDateState.value.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                    }
                    
                    // Apply button
                    BaseButton(
                        onClick = {
                            onConfirm(
                                currentStartDate.value,
                                currentEndDate.value
                            )
                        },
                        text = "Apply",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDateRangeFilter() {
    val startDate = remember { mutableStateOf("2023-01-01") }
    val endDate = remember { mutableStateOf("2023-01-31") }

    DateRangeFilter(
        startDate = startDate.value,
        endDate = endDate.value,
        onDateRangeSelected = { newStartDate, newEndDate ->
            startDate.value = newStartDate
            endDate.value = newEndDate
        }
    )
}
