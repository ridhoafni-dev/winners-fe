package com.dailyapps.feature.observation.page.form

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.dailyapps.common.Danger600
import com.dailyapps.common.Neutral100
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Neutral700
import com.dailyapps.common.Neutral900
import com.dailyapps.common.Primary
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseButton
import com.dailyapps.common.components.BaseTextField
import com.dailyapps.common.components.DateRangeButton
import com.dailyapps.common.components.TextFieldDropdown
import com.dailyapps.common.fontLight
import com.dailyapps.common.utils.httpFormat
import com.dailyapps.feature.observation.ObservationViewModel
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.feature.observation.state.ObservationState
import com.dailyapps.observation.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ObservationFormScreen(
    navController: NavHostController,
    viewModel: ObservationViewModel,
    observationId: Long? = null,  // Added parameter for edit mode
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value
    val isEditMode = observationId != null && observationId != 0L

    // Load observation data if in edit mode
    LaunchedEffect(observationId, currentState.token) {
        if (isEditMode && observationId != null) {
            if (currentState.token.isEmpty()) return@LaunchedEffect

            viewModel.handleAction(
                ObservationAction.OnGetObservation(observationId, currentState.token)
            )
        }
    }

    // Populate form with observation data when in edit mode
    LaunchedEffect(currentState.detail.observation) {
        if (isEditMode) {
            val observation = currentState.detail.observation
            viewModel.handleAction(
                ObservationAction.OnObservationValueChange(
                    name = observation.name ?: "",
                    description = observation.description ?: "",
                    date = observation.createAt ?: "",
                    lecturerId = observation.observationLecturer?.userId ?: 0L,
                    imageUri = observation.image
                )
            )
        }
    }

    val onValueChanged = { name: String, description: String, date: String, lecturerId: Long, imageUri: String? ->
        viewModel.handleAction(
            ObservationAction.OnObservationValueChange(
                name = name,
                description = description,
                date = date,
                lecturerId = lecturerId,
                imageUri = imageUri
            )
        )
    }

    val onSubmit = {
        if (isEditMode) {
            viewModel.handleAction(ObservationAction.OnUpdateObservation(observationId ?: 0L))
        } else {
            viewModel.handleAction(ObservationAction.OnSubmitObservation)
        }
    }

    val onSuccess = {
        navController.popBackStack()
        viewModel.handleAction(
            ObservationAction.OnResetState
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(if (isEditMode) R.string.title_observation_edit else R.string.title_observation_add),
                onClickBack = { navController.popBackStack() },
                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {},
            )
        }) { paddingValues ->
        ObservationFormContent(
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
fun ObservationFormContent(
    state: ObservationState,
    modifier: Modifier,
    onValueChanged: (name: String, description: String, date: String, lecturer: Long, image: String?) -> Unit = { _, _, _, _, _ -> },
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
    val description = currentState.description
    val date = currentState.date
    val selectedLecture = currentState.lecturerId
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }

    // Calendar instance for date picker
    val calendar = Calendar.getInstance()

    // Format for internal date storage - yyyy-MM-dd
    val dateFormatInternal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Format for date display
    val dateFormatDisplay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Formatted date for display
    val displayDate = remember(date) {
        if (date.isEmpty()) {
            ""
        } else {
            try {
                // Parse with internal format and format with display format
                val parsedDate = dateFormatInternal.parse(date)
                if (parsedDate != null) {
                    dateFormatDisplay.format(parsedDate)
                } else {
                    date
                }
            } catch (e: Exception) {
                date
            }
        }
    }

    // Set calendar to current date in state if available
    if (date.isNotEmpty()) {
        try {
            calendar.time = dateFormatInternal.parse(date) ?: calendar.time
        } catch (e: Exception) {
            // Use current date if parsing fails
        }
    }

    var isRemoteImage by remember { mutableStateOf(false) }

    // Show date picker dialog when showDatePicker is true
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Format the selected date
                val newDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                // Update the date using onValueChanged callback
                onValueChanged(name, description, newDate, selectedLecture, currentState.image.ifEmpty { imageUri?.toString() })
                // Hide the date picker
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePicker = false }
            show()
        }
    }

// Don't try to convert remote URLs to URIs
    LaunchedEffect(currentState.image) {
        if (currentState.image.isNotEmpty()) {
            if (currentState.image.startsWith("content://") ||
                currentState.image.startsWith("file://")) {
                // It's a local URI
                imageUri = currentState.image.toUri()
                isRemoteImage = false
            } else {
                // It's a remote URL
                imageUri = null
                isRemoteImage = true
            }
        }
        else {
            // Clear image states when image is empty
            imageUri = null
            isRemoteImage = false
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            onValueChanged(name, description, date, selectedLecture, uri.toString())
        }
    }

    // Handle success state with LaunchedEffect
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onSuccess()
        }
    }

    // Add function to clear image
    fun clearImage() {
        imageUri = null
        isRemoteImage = false
        onValueChanged(name, description, date, selectedLecture, "")
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Show error message if there is one
        if (isError) {
            Text(
                text = errorMessage,
                color = androidx.compose.ui.graphics.Color.Red,
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
                    currentName, description, date, selectedLecture, currentState.image.ifEmpty { imageUri?.toString() }
                )
            },
            placeholder = stringResource(R.string.name),
            enable = !isLoading
        )

        // Description field
        BaseTextField(
            modifier = Modifier.padding(top = 16.dp),
            value = description,
            title = stringResource(R.string.description),
            keyboardType = KeyboardType.Text,
            onValueChange = { currentDescription ->
                onValueChanged(
                    name, currentDescription, date, selectedLecture, imageUri?.toString()
                )
            },
            placeholder = stringResource(R.string.description),
            enable = !isLoading
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
                        name, description, date, lecturer.id?.toLong() ?: 0L, imageUri?.toString()
                    )
                }
            },
            enabled = !isLoading
        )

        // Date field
        DateRangeButton(
            modifier = Modifier.padding(top = 16.dp),
            text = displayDate.ifEmpty { stringResource(R.string.date) },
            label = stringResource(R.string.date),
            onClick = { showDatePicker = true },
            enabled = !isLoading
        )

        // Modern Image Picker with Clear Button
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Image",
                    fontFamily = fontLight,
                    fontSize = 16.sp,
                    color = Neutral900,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if ((imageUri != null) || (isRemoteImage && currentState.image.isNotEmpty())) {
                    TextButton(
                        onClick = { clearImage() },
                        enabled = !isLoading,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear image",
                            tint = Danger600,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clear",
                            color = Danger600,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if ((imageUri != null) || (isRemoteImage && currentState.image.isNotEmpty())) {
                // Image is selected - show preview with edit/clear options
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Neutral100, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isLoading) { imagePickerLauncher.launch("image/*") }
                ) {
                    // Image preview
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = if (imageUri != null) imageUri else currentState.image.httpFormat(),
                            onError = {
                                // If image loading fails, clear the image if in edit mode
                                if (isEditMode && isRemoteImage) {
                                    clearImage()
                                }
                            }
                        ),
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Edit overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Edit button
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    color = Primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Change image",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxSize()
                                    )
                                }
                            }
                            
                            Text(
                                text = "Change Image",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            } else {
                // No image - show placeholder with dashed border
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .drawWithCache {
                            val strokeWidth = 2.dp.toPx()
                            val dashLength = 10.dp.toPx()
                            val dashGap = 5.dp.toPx()
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, dashGap), 0f)

                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    color = Neutral300,
                                    style = Stroke(
                                        width = strokeWidth,
                                        pathEffect = pathEffect
                                    )
                                )
                            }
                        }
                        .clickable(enabled = !isLoading) { imagePickerLauncher.launch("image/*") }
                        .background(Neutral100.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = Primary.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add image",
                                tint = Primary,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize()
                            )
                        }

                        Text(
                            text = "Tap to select image",
                            color = Neutral700,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 12.dp)
                        )

                        Text(
                            text = "JPG or PNG recommended",
                            color = Neutral300,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button - now shows loading state
        BaseButton(
            modifier = Modifier
                .padding(top = 26.dp)
                .height(56.dp)
                .fillMaxWidth(),
            text = stringResource(if (isEditMode) R.string.update else R.string.submit),
            isLoading = isLoading,
            enabled = !isLoading
        ) {
            onSubmit()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
