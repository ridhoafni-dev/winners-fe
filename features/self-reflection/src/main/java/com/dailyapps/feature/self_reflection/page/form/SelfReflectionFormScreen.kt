package com.dailyapps.feature.self_reflection.page.form

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseButton
import com.dailyapps.common.components.BaseTextField
import com.dailyapps.common.components.TextFieldDropdown
import com.dailyapps.feature.selfreflection.SelfReflectionViewModel
import com.dailyapps.feature.selfreflection.state.SelfReflectionAction
import com.dailyapps.feature.selfreflection.state.SelfReflectionState

@Composable
fun SelfReflectionFormScreen(
    navController: NavHostController,
    viewModel: SelfReflectionViewModel,
    selfReflectionId: Long? = null,  // Parameter for edit mode
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value
    val isEditMode = selfReflectionId != null && selfReflectionId != 0L

    // Load self reflection data if in edit mode
    LaunchedEffect(selfReflectionId, currentState.token) {
        if (isEditMode && selfReflectionId != null) {
            if (currentState.token.isEmpty()) return@LaunchedEffect

            viewModel.handleAction(
                SelfReflectionAction.OnGetSelfReflection(selfReflectionId, currentState.token)
            )
        }
    }

    // Populate form with self reflection data when in edit mode
    LaunchedEffect(currentState.detail.selfReflection) {
        if (isEditMode) {
            val selfReflection = currentState.detail.selfReflection
            viewModel.handleAction(
                SelfReflectionAction.OnSelfReflectionValueChange(
                    title = selfReflection?.title ?: "",
                    lecturerId = selfReflection?.selfReflectionLecturer?.userId ?: 0L
                )
            )
        }
    }

    val onValueChanged = { title: String, lecturerId: Long ->
        viewModel.handleAction(
            SelfReflectionAction.OnSelfReflectionValueChange(
                title = title,
                lecturerId = lecturerId
            )
        )
    }

    val onSubmit = {
        if (isEditMode) {
            viewModel.handleAction(SelfReflectionAction.OnUpdateSelfReflection(selfReflectionId ?: 0L))
        } else {
            viewModel.handleAction(SelfReflectionAction.OnSubmitSelfReflection)
        }
    }

    val onSuccess = {
        navController.popBackStack()
        viewModel.handleAction(
            SelfReflectionAction.OnResetState
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            BaseAppBar(
                title = if (isEditMode) "Edit Self-Reflection" else "Add Self-Reflection",
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

@Composable
fun FormContent(
    state: SelfReflectionState,
    modifier: Modifier,
    onValueChanged: (title: String, lecturer: Long) -> Unit = { _, _ -> },
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
    val title = currentState.title
    val selectedLecture = currentState.lecturerId

    // Validation state
    var titleError by remember { mutableStateOf("") }
    var lecturerError by remember { mutableStateOf("") }
    var showValidationErrors by remember { mutableStateOf(false) }

    // Validate all fields
    fun validateForm(): Boolean {
        var isValid = true

        // Validate title
        if (title.isBlank()) {
            titleError = "Title is required"
            isValid = false
        } else {
            titleError = ""
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
                text = errorMessage ?: "",
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
                    if (isEditMode) "Self-Reflection updated successfully!" else "Self-Reflection added successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Title field
        BaseTextField(
            value = title,
            title = "Title",
            keyboardType = KeyboardType.Text,
            onValueChange = { currentTitle ->
                onValueChanged(
                    currentTitle, selectedLecture
                )
                if (currentTitle.isNotBlank()) {
                    titleError = ""
                }
            },
            placeholder = "Enter self-reflection title",
            enable = !isLoading,
            isError = titleError.isNotEmpty(),
            errorMessage = titleError
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
            label = "Lecturer",
            itemsDropdown = lecturers.map { it.name ?: "-" },
            onValueChange = { selectedName ->
                lecturers.find { it.name == selectedName }?.let { lecturer ->
                    selectedLecturerName = selectedName // Update immediately for UI responsiveness
                    onValueChanged(
                        title, lecturer.id?.toLong() ?: 0L
                    )
                    lecturerError = "" // Clear error when an item is selected
                }
            },
            enabled = !isLoading,
            isError = lecturerError.isNotEmpty(),
            errorMessage = lecturerError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button - now shows loading state and performs validation
        BaseButton(
            modifier = Modifier
                .padding(top = 26.dp)
                .height(56.dp)
                .fillMaxWidth(),
            text = if (isEditMode) "Update" else "Submit",
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
