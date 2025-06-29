package com.dailyapps.feature.report.page.form

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dailyapps.common.Danger600
import com.dailyapps.common.Neutral100
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Neutral700
import com.dailyapps.common.Neutral900
import com.dailyapps.common.Primary
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseButton
import com.dailyapps.common.components.DateRangeButton
import com.dailyapps.common.components.TextFieldDropdown
import com.dailyapps.common.fontLight
import com.dailyapps.feature.report.R
import com.dailyapps.feature.report.ReportViewModel
import com.dailyapps.feature.report.state.ReportAction
import com.dailyapps.feature.report.state.ReportState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ReportFormScreen(
    navController: NavHostController,
    viewModel: ReportViewModel,
    reportId: Long? = null,  // Added parameter for edit mode
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value
    val isEditMode = reportId != null && reportId != 0L

    // Load report data if in edit mode
    LaunchedEffect(reportId, currentState.token) {
        if (isEditMode && reportId != null) {
            if (currentState.token.isEmpty()) return@LaunchedEffect

            viewModel.handleAction(
                ReportAction.OnGetReport(reportId, currentState.token)
            )
        }
    }

    // Populate form with report data when in edit mode
    LaunchedEffect(currentState.detail.report) {
        if (isEditMode) {
            val report = currentState.detail.report
            viewModel.handleAction(
                ReportAction.OnReportValueChange(
                    date = report.createAt ?: "",
                    lecturerId = report.reportLecturer?.userId?.toLong() ?: 0L,
                    documentUri = report.image
                )
            )
        }
    }

    val onValueChanged = { date: String, lecturerId: Long, documentUri: String? ->
        viewModel.handleAction(
            ReportAction.OnReportValueChange(
                date = date,
                lecturerId = lecturerId,
                documentUri = documentUri
            )
        )
    }

    val onSubmit = {
        if (isEditMode) {
            viewModel.handleAction(ReportAction.OnUpdateReport(reportId ?: 0L))
        } else {
            viewModel.handleAction(ReportAction.OnSubmitReport)
        }
    }

    val onSuccess = {
        navController.popBackStack()
        viewModel.handleAction(
            ReportAction.OnResetState
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(if (isEditMode) R.string.title_report_edit else R.string.title_report_add),
                onClickBack = { navController.popBackStack() },
                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {},
            )
        }) { paddingValues ->
        ReportFormContent(
            modifier = modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
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
fun ReportFormContent(
    state: ReportState,
    modifier: Modifier,
    onValueChanged: (date: String, lecturer: Long, documentUri: String?) -> Unit = { _, _, _ -> },
    onSubmit: () -> Unit = {},
    onSuccess: () -> Unit = {},
    isEditMode: Boolean = false
) {
    val context = LocalContext.current

    val currentState = state.form
    val detailState = state.detail
    val isLoading = state.isLoading
    val isError = state.isError
    val errorMessage = state.errorMessage
    val isSuccess = state.isSuccess

    // Form state
    val date = currentState.date
    val selectedLecture = detailState.report.reportLecturer?.id?.toLong() ?: 0L
    var documentUri by remember { mutableStateOf<Uri?>(null) }

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
            } catch (_: Exception) {
                date
            }
        }
    }

    // Set calendar to current date in state if available
    if (date.isNotEmpty()) {
        try {
            calendar.time = dateFormatInternal.parse(date) ?: calendar.time
        } catch (e: Exception) {
            e.printStackTrace()
            // Use current date if parsing fails
        }
    }

    var isRemoteDocument by remember { mutableStateOf(false) }

    // Show date picker dialog when showDatePicker is true
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Format the selected date
                val newDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                // Update the date using onValueChanged callback
                onValueChanged(newDate, selectedLecture, currentState.documentUri.ifEmpty { documentUri?.toString() })
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

    // Handle document URI from state
    LaunchedEffect(currentState.documentUri) {
        if (currentState.documentUri.isNotEmpty()) {
            if (currentState.documentUri.startsWith("content://") ||
                currentState.documentUri.startsWith("file://")) {
                // It's a local URI
                documentUri = Uri.parse(currentState.documentUri)
                isRemoteDocument = false
            } else {
                // It's a remote URL
                documentUri = null
                isRemoteDocument = true
            }
        } else {
            // Clear document states when URI is empty
            documentUri = null
            isRemoteDocument = false
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            documentUri = uri
            isRemoteDocument = false
            onValueChanged(date, selectedLecture, uri.toString())
        }
    }

    // Handle success state with LaunchedEffect
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onSuccess()
        }
    }

    // Add function to clear document
    fun clearDocument() {
        documentUri = null
        isRemoteDocument = false
        onValueChanged(date, selectedLecture, "")
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
        LaunchedEffect(isSuccess) {
            if (isSuccess) {
                Toast.makeText(
                    context,
                    if (isEditMode) "Report updated successfully!" else "Report submitted successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Lecturers Dropdown list
        val lecturers = state.form.lecturers

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
            modifier = Modifier,
            text = selectedLecturerName,
            label = stringResource(R.string.lecturer),
            itemsDropdown = lecturers.map { it.name ?: "-" },
            onValueChange = { selectedName ->
                lecturers.find { it.name == selectedName }?.let { lecturer ->
                    selectedLecturerName = selectedName // Update immediately for UI responsiveness
                    onValueChanged(
                        date, lecturer.id?.toLong() ?: 0L, currentState.documentUri.ifEmpty { documentUri?.toString() }
                    )
                }
            },
            enabled = !isLoading
        )

        // Date field
        Text(
            text = "Date",
            fontFamily = fontLight,
            fontSize = 16.sp,
            color = Neutral900,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        DateRangeButton(
            modifier = Modifier,
            text = displayDate.ifEmpty { stringResource(R.string.date) },
            label = stringResource(R.string.date),
            onClick = { showDatePicker = true },
            enabled = !isLoading
        )

        // Document Picker
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
                    text = "Document",
                    fontFamily = fontLight,
                    fontSize = 16.sp,
                    color = Neutral900,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if ((documentUri != null) || (isRemoteDocument && currentState.documentUri.isNotEmpty())) {
                    TextButton(
                        onClick = { clearDocument() },
                        enabled = !isLoading,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear document",
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

            if ((documentUri != null) || (isRemoteDocument && currentState.documentUri.isNotEmpty())) {
                // Document is selected - show preview info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Neutral100, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isLoading) { documentPickerLauncher.launch("*/*") }
                        .background(Neutral100.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Document",
                                tint = Primary,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Document selected",
                                color = Neutral900,
                                fontWeight = FontWeight.Medium
                            )

                            // Extract file name from URI if possible
                            val fileName = remember(documentUri, currentState.documentUri) {
                                when {
                                    documentUri != null -> {
                                        documentUri?.lastPathSegment?.substringAfterLast('/')
                                            ?: "Tap to change"
                                    }
                                    isRemoteDocument -> {
                                        currentState.documentUri.substringAfterLast("/")
                                            .ifEmpty { "Remote document" }
                                    }
                                    else -> "Tap to change"
                                }
                            }

                            Text(
                                text = fileName,
                                color = Neutral700,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            } else {
                // No document - show placeholder with dashed border
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .drawWithCache {
                            val strokeWidth = 2.dp.toPx()
                            val dashLength = 10.dp.toPx()
                            val dashGap = 5.dp.toPx()
                            val pathEffect =
                                PathEffect.dashPathEffect(floatArrayOf(dashLength, dashGap), 0f)

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
                        .clickable(enabled = !isLoading) { documentPickerLauncher.launch("*/*") }
                        .background(Neutral100.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = Primary.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = "Upload document",
                                tint = Primary,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize()
                            )
                        }

                        Text(
                            text = "Tap to select document",
                            color = Neutral700,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 12.dp)
                        )

                        Text(
                            text = "PDF, DOC, or DOCX recommended",
                            color = Neutral300,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        BaseButton(
            modifier = Modifier
                .padding(top = 26.dp)
                .height(56.dp)
                .fillMaxWidth(),
            text = stringResource(if (isEditMode) R.string.update else R.string.submit),
            isLoading = isLoading,
            enabled = !isLoading &&
                     ((documentUri != null) || (isRemoteDocument && currentState.documentUri.isNotEmpty())) &&
                     selectedLecture > 0
        ) {
            onSubmit()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
