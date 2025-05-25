package com.dailyapps.feature.observation.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        }
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date Range") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Start Date: ${currentStartDate.value}")
                    Button(onClick = {
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
                    }) {
                        Text("Select")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("End Date: ${currentEndDate.value}")
                    Button(onClick = {
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
                    }) {
                        Text("Select")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    currentStartDate.value,
                    currentEndDate.value
                )
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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