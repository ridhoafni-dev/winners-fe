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
    val context = LocalContext.current

    // Load self-reflection data if in edit mode
    LaunchedEffect(selfReflectionId, currentState.token) {
        if (isEditMode && selfReflectionId != null) {
            if (currentState.token.isEmpty()) return@LaunchedEffect

            viewModel.handleAction(
                SelfReflectionAction.OnGetSelfReflection(selfReflectionId, currentState.token)
            )
        }
    }

    // Reset state when leaving the screen
    LaunchedEffect(Unit) {
        return@LaunchedEffect
    }

    // Handle success state
    LaunchedEffect(key1 = currentState.add.isSuccess, key2 = currentState.update.isSuccess) {
        if (currentState.add.isSuccess || currentState.update.isSuccess) {
            Toast.makeText(
                context,
                if (isEditMode) "Refleksi diri berhasil diperbarui" else "Refleksi diri berhasil ditambahkan",
                Toast.LENGTH_SHORT
            ).show()
            navController.popBackStack()
        }
    }

    // Handle error state
    LaunchedEffect(key1 = currentState.add.errorMessage, key2 = currentState.update.errorMessage) {
        val errorMessage = currentState.add.errorMessage ?: currentState.update.errorMessage
        if (errorMessage != null) {
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            BaseAppBar(
                title = if (isEditMode) "Edit Refleksi Diri" else "Tambah Refleksi Diri",
                onClickBack = {
                    navController.popBackStack()
                },
                modifier = modifier,
                menuIconResource = null,
                elevation = 4.dp,
                subTitle = null,
                onMenuClick = { }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title field
            BaseTextField(
                value = if (isEditMode) currentState.update.title else currentState.add.title,
                onValueChange = { title ->
                    viewModel.handleAction(SelfReflectionAction.OnSelfReflectionValueChange(
                        title = title,
                        lecturerId = if (isEditMode) currentState.update.lecturerId else currentState.add.lecturerId
                    ))
                },
                label = "Judul",
                placeholder = "Masukkan judul refleksi diri",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lecturer dropdown field
            if (currentState.role != "lecturer") { // Only show lecturer dropdown for students
                val selectedLecturerId = if (isEditMode) currentState.update.lecturerId else currentState.add.lecturerId
                val selectedLecturer = if (isEditMode) {
                    currentState.update.lecturers.find { it.id?.toLong() == selectedLecturerId }
                } else {
                    currentState.add.lecturers.find { it.id?.toLong() == selectedLecturerId }
                }

                val lecturerItems = if (isEditMode) {
                    currentState.update.lecturers.map { it.name ?: "-" }
                } else {
                    currentState.add.lecturers.map { it.name ?: "-" }
                }

                TextFieldDropdown(
                    value = selectedLecturer?.name ?: "",
                    onValueChange = { },
                    label = "Dosen",
                    placeholder = "Pilih dosen",
                    items = lecturerItems,
                    onItemSelected = { index ->
                        val selectedId = if (isEditMode) {
                            currentState.update.lecturers.getOrNull(index)?.id?.toLong() ?: 0L
                        } else {
                            currentState.add.lecturers.getOrNull(index)?.id?.toLong() ?: 0L
                        }

                        viewModel.onAction(SelfReflectionAction.OnSelfReflectionValueChange(
                            title = if (isEditMode) currentState.update.title else currentState.add.title,
                            lecturerId = selectedId
                        ))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Submit button
            BaseButton(
                text = if (isEditMode) "Perbarui" else "Simpan",
                onClick = {
                    if (isEditMode) {
                        viewModel.onAction(SelfReflectionAction.OnUpdateSelfReflection(selfReflectionId!!))
                    } else {
                        viewModel.onAction(SelfReflectionAction.OnSubmitSelfReflection)
                    }
                },
                isLoading = if (isEditMode) currentState.update.isLoading else currentState.add.isLoading,
                enabled = if (isEditMode) {
                    currentState.update.title.isNotEmpty() &&
                    (currentState.role == "lecturer" || currentState.update.lecturerId != 0L)
                } else {
                    currentState.add.title.isNotEmpty() &&
                    (currentState.role == "lecturer" || currentState.add.lecturerId != 0L)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
