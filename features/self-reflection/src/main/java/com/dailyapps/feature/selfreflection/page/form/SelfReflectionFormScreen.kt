package com.dailyapps.feature.selfreflection.page.form

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
    val scrollState = rememberScrollState()

    // Load self-reflection data if in edit mode
    LaunchedEffect(selfReflectionId, currentState.token) {
        if (isEditMode && selfReflectionId != null) {
            if (currentState.token.isEmpty()) return@LaunchedEffect

            viewModel.handleAction(
                SelfReflectionAction.OnGetSelfReflection(selfReflectionId, currentState.token)
            )
        }
    }

    // Populate form with data when in edit mode
    LaunchedEffect(currentState.detail.selfReflection) {
        if (isEditMode) {
            val selfReflection = currentState.detail.selfReflection
            viewModel.handleAction(
                SelfReflectionAction.OnSelfReflectionValueChange(
                    title = selfReflection?.title ?: "",
                    lecturerId = selfReflection?.selfReflectionLecturer?.userId?.toLong() ?: 0L
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
            viewModel.handleAction(SelfReflectionAction.OnUpdateSelfReflection(selfReflectionId!!))
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

    Scaffold(
        topBar = {
            BaseAppBar(
                title = if (isEditMode) "Edit Refleksi Diri" else "Tambah Refleksi Diri",
                onClickBack = { navController.popBackStack() },
                modifier = modifier,
                menuIconResource = null,
                elevation = 1.dp,
                subTitle = null,
                onMenuClick = { }
            )
        }
    ) { innerPadding ->
        FormContent(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState),
            state = currentState,
            onValueChanged = onValueChanged,
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
    onValueChanged: (title: String, lecturerId: Long) -> Unit = { _, _ -> },
    onSubmit: () -> Unit = {},
    onSuccess: () -> Unit = {},
    isEditMode: Boolean = false
) {
    val context = LocalContext.current

    val isLoading = if (isEditMode) state.update.isLoading else state.add.isLoading
    val isSuccess = if (isEditMode) state.update.isSuccess else state.add.isSuccess
    val errorMessage = if (isEditMode) state.update.errorMessage else state.add.errorMessage

    // Form state
    val title = if (isEditMode) state.update.title else state.add.title
    val selectedLecturerId = if (isEditMode) state.update.lecturerId else state.add.lecturerId
    val lecturers = if (isEditMode) state.update.lecturers else state.add.lecturers

    // Validation state
    var titleError by remember { mutableStateOf("") }
    var lecturerError by remember { mutableStateOf("") }
    var showValidationErrors by remember { mutableStateOf(false) }

    // Validate all fields
    fun validateForm(): Boolean {
        var isValid = true

        // Validate title
        if (title.isBlank()) {
            titleError = "Judul refleksi diri diperlukan"
            isValid = false
        } else {
            titleError = ""
        }

        // Validate lecturer selection if user is not a lecturer
        if (state.role != "LECTURER" && selectedLecturerId <= 0) {
            lecturerError = "Pilih dosen pembimbing"
            isValid = false
        } else {
            lecturerError = ""
        }

        showValidationErrors = !isValid
        return isValid
    }

    // Remember the selected lecturer name across recompositions
    var selectedLecturerName by remember(selectedLecturerId, lecturers) {
        mutableStateOf(lecturers.find { it.id?.toLong() == selectedLecturerId }?.name ?: "")
    }

    // Update the selected lecturer name when either lecturers or selectedLecturerId changes
    LaunchedEffect(lecturers, selectedLecturerId) {
        if (lecturers.isNotEmpty()) {
            val lecturer = lecturers.find { it.id?.toLong() == selectedLecturerId }
            selectedLecturerName = lecturer?.name ?: ""
        }
    }

    // Handle success state with LaunchedEffect
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(
                context,
                if (isEditMode) "Refleksi diri berhasil diperbarui" else "Refleksi diri berhasil ditambahkan",
                Toast.LENGTH_SHORT
            ).show()
            onSuccess()
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Show error message if there is one from the server
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        // Title field with validation
        BaseTextField(
            value = title,
            title = "Judul",
            keyboardType = KeyboardType.Text,
            onValueChange = { currentTitle ->
                onValueChanged(
                    currentTitle, selectedLecturerId
                )
                if (currentTitle.isNotBlank()) {
                    titleError = ""
                }
            },
            placeholder = "Masukkan judul refleksi diri",
            enable = !isLoading,
            isError = showValidationErrors && titleError.isNotEmpty(),
            errorMessage = if (showValidationErrors) titleError else ""
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lecturer dropdown field - only show for students
        if (state.role != "LECTURER") {
            TextFieldDropdown(
                modifier = Modifier.fillMaxWidth(),
                text = selectedLecturerName,
                label = "Dosen Pembimbing",
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
                isError = showValidationErrors && lecturerError.isNotEmpty(),
                errorMessage = if (showValidationErrors) lecturerError else ""
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Submit button - now shows loading state and performs validation
        BaseButton(
            modifier = Modifier
                .padding(top = 16.dp)
                .height(56.dp)
                .fillMaxWidth(),
            text = if (isEditMode) "Perbarui" else "Simpan",
            isLoading = isLoading,
            enabled = !isLoading
        ) {
            if (validateForm()) {
                onSubmit()
            } else {
                // Show toast for validation failure
                Toast.makeText(
                    context,
                    "Harap perbaiki kesalahan pada formulir",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
