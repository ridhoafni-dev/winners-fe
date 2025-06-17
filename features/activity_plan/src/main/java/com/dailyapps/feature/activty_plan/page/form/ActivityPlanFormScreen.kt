package com.dailyapps.feature.activty_plan.page.form

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dailyapps.activity_plan.R
import com.dailyapps.common.Neutral900
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseButton
import com.dailyapps.common.components.BaseTextField
import com.dailyapps.common.components.DateRangeButton
import com.dailyapps.common.components.TextFieldDropdown
import com.dailyapps.common.fontLight
import com.dailyapps.feature.activty_plan.ActivityPlanViewModel
import com.dailyapps.feature.activty_plan.state.ActivityPlanAction
import com.dailyapps.feature.activty_plan.state.ActivityPlanState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ActivityPlanFormScreen(
    navController: NavHostController,
    viewModel: ActivityPlanViewModel,
    activityPlanId: Long? = null,  // Added parameter for edit mode
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value
    val isEditMode = activityPlanId != null && activityPlanId != 0L

    // Set default end date to today if not in edit mode
    LaunchedEffect(Unit) {
        if (!isEditMode) {
            // Get today's date formatted as yyyy-MM-dd
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

            viewModel.handleAction(
                ActivityPlanAction.OnActivityPlanValueChange(
                    name = currentState.add.name,
                    startDate = currentState.add.startDate,
                    endDate = today,
                    lecturerId = currentState.add.lecturerId,
                    status = currentState.add.status
                )
            )
        }
    }

    // Load observation data if in edit mode
    LaunchedEffect(activityPlanId, currentState.token) {
        if (isEditMode && activityPlanId != null) {
            if (currentState.token.isEmpty()) return@LaunchedEffect

            viewModel.handleAction(
                ActivityPlanAction.OnGetActivityPlan(activityPlanId, currentState.token)
            )
        }
    }

    // Populate form with observation data when in edit mode
    LaunchedEffect(currentState.detail.activityPlan) {
        if (isEditMode) {
            val observation = currentState.detail.activityPlan
            viewModel.handleAction(
                ActivityPlanAction.OnActivityPlanValueChange(
                    name = observation.name ?: "",
                    startDate = observation.startDate ?: "",
                    endDate = observation.endDate ?: "",
                    lecturerId = observation.activityPlanLecturer?.userId?.toLong() ?: 0L,
                    status = observation.status ?: "Aktif"
                )
            )
        }
    }

    val onValueChanged = { name: String, startDate: String, endDate: String, lecturerId: Long, status: String ->
        viewModel.handleAction(
            ActivityPlanAction.OnActivityPlanValueChange(
                name = name,
                startDate = startDate,
                endDate = endDate,
                lecturerId = lecturerId,
                status = status
            )
        )
    }

    val onSubmit = {
        if (isEditMode) {
            viewModel.handleAction(ActivityPlanAction.OnUpdateActivityPlan(activityPlanId ?: 0L))
        } else {
            viewModel.handleAction(ActivityPlanAction.OnSubmitActivityPlan)
        }
    }

    val onSuccess = {
        navController.popBackStack()
        viewModel.handleAction(
            ActivityPlanAction.OnResetState
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(if (isEditMode) R.string.observation_edit_title else R.string.observation_add_title),
                onClickBack = { navController.popBackStack() },
                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {},
            )
        }) { paddingValues ->
        FormContent(
            modifier = modifier
                .padding(paddingValues)
                .verticalScroll(scrollState), // Add vertical scroll capability
            onValueChanged = onValueChanged,
            state = currentState,
            onSubmit = onSubmit,
            onSuccess = onSuccess,
            isEditMode = isEditMode
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun FormContent(
    state: ActivityPlanState,
    modifier: Modifier,
    onValueChanged: (name: String, startDate: String, endDate: String, lecturer: Long, status: String) -> Unit = { _, _, _, _, _ -> },
    onSubmit: () -> Unit = {},
    onSuccess: () -> Unit = {},
    isEditMode: Boolean = false
) {
    val context = LocalContext.current

    val currentState = state.add
    val isLoading = state.isLoading
    val isError = state.isError
    val errorMessage = state.errorMessage
    val isSuccess = state.isSuccess

    // Form state
    val name = currentState.name
    val startDate = currentState.startDate
    val endDate = currentState.endDate
    val selectedLecture = currentState.lecturerId
    val status = currentState.status

    // Validation state
    var nameError by remember { mutableStateOf("") }
    var startDateError by remember { mutableStateOf("") }
    var endDateError by remember { mutableStateOf("") }
    var lecturerError by remember { mutableStateOf("") }
    var showValidationErrors by remember { mutableStateOf(false) }

    // State for showing the date pickers
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Calendar instance for date picker
    val calendar = Calendar.getInstance()

    // Format for internal date storage - yyyy-MM-dd
    val dateFormatInternal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Format for date display
    val dateFormatDisplay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Formatted start date for display
    val displayStartDate = remember(startDate) {
        if (startDate.isEmpty()) {
            ""
        } else {
            try {
                // Parse with internal format and format with display format
                val parsedDate = dateFormatInternal.parse(startDate)
                if (parsedDate != null) {
                    dateFormatDisplay.format(parsedDate)
                } else {
                    startDate
                }
            } catch (e: Exception) {
                startDate
            }
        }
    }

    // Formatted end date for display
    val displayEndDate = remember(endDate) {
        if (endDate.isEmpty()) {
            ""
        } else {
            try {
                // Parse with internal format and format with display format
                val parsedDate = dateFormatInternal.parse(endDate)
                if (parsedDate != null) {
                    dateFormatDisplay.format(parsedDate)
                } else {
                    endDate
                }
            } catch (e: Exception) {
                endDate
            }
        }
    }

    // Validate all fields
    fun validateForm(): Boolean {
        var isValid = true

        // Validate name
        if (name.isBlank()) {
            nameError = "Name is required"
            isValid = false
        } else {
            nameError = ""
        }

        // Validate start date
        if (startDate.isBlank()) {
            startDateError = "Start date is required"
            isValid = false
        } else {
            startDateError = ""
        }

        // Validate end date
        if (endDate.isBlank()) {
            endDateError = "End date is required"
            isValid = false
        } else {
            // Validate that end date is not before start date
            if (startDate.isNotBlank()) {
                try {
                    val parsedStartDate = dateFormatInternal.parse(startDate)
                    val parsedEndDate = dateFormatInternal.parse(endDate)
                    if (parsedStartDate != null && parsedEndDate != null && parsedEndDate.before(parsedStartDate)) {
                        endDateError = "End date cannot be before start date"
                        isValid = false
                    } else {
                        endDateError = ""
                    }
                } catch (e: Exception) {
                    endDateError = "Invalid date format"
                    isValid = false
                }
            } else {
                endDateError = ""
            }
        }

        // Validate lecturer selection
        if (selectedLecture <= 0) {
            lecturerError = "Lecturer selection is required"
            isValid = false
        } else {
            lecturerError = ""
        }

        showValidationErrors = !isValid
        return isValid
    }

    // Set calendar to current date in state if available
    if (startDate.isNotEmpty()) {
        try {
            calendar.time = dateFormatInternal.parse(startDate) ?: calendar.time
        } catch (e: Exception) {
            // Use current date if parsing fails
        }
    }

    // Show start date picker dialog when showStartDatePicker is true
    if (showStartDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Format the selected date
                val newStartDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                // Update the date using onValueChanged callback
                onValueChanged(name, newStartDate, endDate, selectedLecture, status)
                // Hide the date picker
                showStartDatePicker = false
                // Clear error if set
                startDateError = ""
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showStartDatePicker = false }
            show()
        }
    }

    // Set calendar for end date if available
    val endDateCalendar = Calendar.getInstance()
    if (endDate.isNotEmpty()) {
        try {
            endDateCalendar.time = dateFormatInternal.parse(endDate) ?: endDateCalendar.time
        } catch (e: Exception) {
            // Use current date if parsing fails
        }
    }

    // Show end date picker dialog when showEndDatePicker is true
    if (showEndDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Format the selected date
                val newEndDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                // Update the date using onValueChanged callback
                onValueChanged(name, startDate, newEndDate, selectedLecture, status)
                // Hide the date picker
                showEndDatePicker = false
                // Clear error if set
                endDateError = ""
            },
            endDateCalendar.get(Calendar.YEAR),
            endDateCalendar.get(Calendar.MONTH),
            endDateCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showEndDatePicker = false }
            show()
        }
    }

    // Handle success state with LaunchedEffect
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onSuccess()
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Show error message if there is one from the server
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        // Show success message if operation succeeded
        LaunchedEffect (isSuccess) {
            if (isSuccess) {
                Toast.makeText(
                    context,
                    if (isEditMode) "Observation updated successfully!" else "Observation added successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Name field
        BaseTextField(
            value = name,
            title = stringResource(R.string.name),
            keyboardType = KeyboardType.Text,
            onValueChange = { currentName ->
                onValueChanged(
                    currentName, startDate, endDate, selectedLecture, status
                )
                if (currentName.isNotBlank()) {
                    nameError = ""
                }
            },
            placeholder = stringResource(R.string.name),
            enable = !isLoading,
            isError = nameError.isNotEmpty(),
            errorMessage = nameError
        )

        // Lecturers Dropdown list
        val lecturers = state.add.lecturers

        // Remember the selected lecturer name across recompositions
        var selectedLecturerName by remember(selectedLecture, lecturers) {
            mutableStateOf(lecturers.find { it.id?.toLong() == selectedLecture }?.name ?: "")
        }

        // Update the selected lecturer name when either lecturers or selectedLecture changes
        LaunchedEffect(lecturers, selectedLecture) {
            if (lecturers.isNotEmpty()) {
                val lecturer = lecturers.find { it.id?.toLong() == selectedLecture }
                selectedLecturerName = lecturer?.name ?: ""
            }
        }

        TextFieldDropdown(
            modifier = Modifier.padding(top = 16.dp),
            text = selectedLecturerName,
            label = stringResource(R.string.lecturer),
            itemsDropdown = lecturers.map { it.name ?: "-" },
            onValueChange = { selectedName ->
                lecturers.find { it.name == selectedName }?.let { lecturer ->
                    selectedLecturerName = selectedName // Update immediately for UI responsiveness
                    onValueChanged(
                        name, startDate, endDate, lecturer.id?.toLong() ?: 0L, status
                    )
                    lecturerError = "" // Clear error when an item is selected
                }
            },
            enabled = !isLoading,
            isError = lecturerError.isNotEmpty(),
            errorMessage = lecturerError
        )

        // Start Date field
        Text(
            text = "Start Date",
            fontFamily = fontLight,
            fontSize = 16.sp,
            color = Neutral900,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        DateRangeButton(
            modifier = Modifier.padding(top = 8.dp),
            text = displayStartDate.ifEmpty { stringResource(R.string.startDate) },
            label = stringResource(R.string.startDate),
            onClick = { showStartDatePicker = true },
            enabled = !isLoading,
            isError = startDateError.isNotEmpty()
        )

        if (startDateError.isNotEmpty()) {
            Text(
                text = startDateError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        // End Date field
        Text(
            text = "End Date",
            fontFamily = fontLight,
            fontSize = 16.sp,
            color = Neutral900,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        DateRangeButton(
            modifier = Modifier.padding(top = 8.dp),
            text = displayEndDate.ifEmpty { stringResource(R.string.endDate) },
            label = stringResource(R.string.endDate),
            onClick = { showEndDatePicker = true },
            enabled = !isLoading,
            isError = endDateError.isNotEmpty()
        )

        if (endDateError.isNotEmpty()) {
            Text(
                text = endDateError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        // Status dropdown - only appears in edit mode
        if (isEditMode) {
            // Status options
            val statusOptions = listOf("Aktif", "Reschedule", "Batal")

            TextFieldDropdown(
                modifier = Modifier.padding(top = 16.dp),
                text = status,
                label = "Status",
                itemsDropdown = statusOptions,
                onValueChange = { selectedStatus ->
                    onValueChanged(
                        name, startDate, endDate, selectedLecture, selectedStatus
                    )
                },
                enabled = !isLoading,
                isError = false
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button - now shows loading state and performs validation
        BaseButton(
            modifier = Modifier
                .padding(top = 26.dp)
                .height(56.dp)
                .fillMaxWidth(),
            text = stringResource(if (isEditMode) R.string.update else R.string.submit),
            isLoading = isLoading,
            enabled = !isLoading
        ) {
            if (validateForm()) {
                onSubmit()
            } else {
                // Show toast for validation failure
                Toast.makeText(
                    context,
                    "Please fix the errors in the form",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
