package com.dailyapps.feature.report.page.list

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dailyapps.common.EmptyList
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Primary500
import com.dailyapps.common.White
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.DateRangeFilter
import com.dailyapps.common.components.ErrorUi
import com.dailyapps.common.components.FontType
import com.dailyapps.common.components.LoadingUi
import com.dailyapps.common.utils.DateTime.Companion.formatDate
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.entity.Report
import com.dailyapps.feature.report.R
import com.dailyapps.feature.report.ReportViewModel
import com.dailyapps.feature.report.state.ReportAction
import com.dailyapps.feature.report.state.ReportState
import kotlinx.coroutines.launch
import com.dailyapps.common.R as commonR

@Composable
fun ReportListScreen(
    navController: NavHostController,
    viewModel: ReportViewModel,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Check if the current user is a lecturer
    val isUserLecturer = remember(currentState.role) {
        currentState.isUserLecturer
    }

    LaunchedEffect(currentState.list.startDate, currentState.list.endDate, currentState.token) {
        if (currentState.token.isEmpty() || currentState.isUserNotExist) return@LaunchedEffect

        viewModel.handleAction(
            ReportAction.OnGetReports(
                userId = currentState.userId,
                startDate = currentState.list.startDate,
                endDate = currentState.list.endDate,
                token = currentState.token
            )
        )
    }

    // Handle download states
    LaunchedEffect(currentState.downloadSuccess, currentState.downloadError) {
        if (currentState.downloadSuccess && currentState.downloadUrl.isNotEmpty()) {
            // Open the URL in browser
            val intent = Intent(Intent.ACTION_VIEW, currentState.downloadUrl.toUri())
            context.startActivity(intent)
            viewModel.handleAction(ReportAction.OnResetDownloadState)
            scope.launch {
                snackbarHostState.showSnackbar(message = context.getString(R.string.download_success))
            }
        } else if (currentState.downloadError) {
            scope.launch {
                snackbarHostState.showSnackbar(message = currentState.downloadErrorMessage.ifEmpty {
                    context.getString(R.string.download_error)
                })
            }
            viewModel.handleAction(ReportAction.OnResetDownloadState)
        }
    }

    val onRetry: () -> Unit = {
        viewModel.handleAction(
            ReportAction.OnGetReports(
                userId = currentState.userId,
                startDate = currentState.list.startDate,
                endDate = currentState.list.endDate,
                token = currentState.token
            )
        )
    }

    ListContent(
        navController = navController,
        state = state.value,
        isUserLecturer = isUserLecturer,
        snackbarHostState = snackbarHostState,
        onDatePickerChange = { startDate, endDate ->
            viewModel.handleAction(
                ReportAction.OnUpdateDateRange(
                    startDate = startDate,
                    endDate = endDate,
                )
            )
            viewModel.handleAction(
                ReportAction.OnGetReports(
                    userId = currentState.userId,
                    startDate = startDate,
                    endDate = endDate,
                    token = currentState.token
                )
            )
        },
        onDownloadReport = { reportId ->
            viewModel.handleAction(
                ReportAction.OnDownloadReport(
                    id = reportId,
                    token = currentState.token
                )
            )
        },
        onEditReport = { report ->
            report.id?.let { reportId ->
                navController.navigate("${NavRoute.formReportScreen}/${reportId}")
            }
        },
        onRetry = onRetry
    )
}

@Composable
fun ListContent(
    navController: NavHostController,
    state: ReportState,
    isUserLecturer: Boolean = false,
    snackbarHostState: SnackbarHostState,
    onDatePickerChange: (String, String) -> Unit,
    onDownloadReport: (Long) -> Unit,
    onEditReport: (Report) -> Unit,
    onRetry: () -> Unit = {}
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.reports_title),
                onClickBack = { navController.popBackStack() },
                menuIconResource = if (!isUserLecturer) commonR.drawable.ic_add else null,
                elevation = 1.dp
            ) {
                if (!isUserLecturer) {
                    navController.navigate("${NavRoute.formReportScreen}/0")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Column {
                // Date range filter
                DateRangeFilter(
                    modifier = Modifier.padding(top = 8.dp),
                    startDate = state.list.startDate,
                    endDate = state.list.endDate,
                    onDateRangeSelected = { startDate, endDate ->
                        onDatePickerChange(startDate, endDate)
                    },
                )

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoadingUi(modifier = Modifier.align(Alignment.Center))
                    }
                } else if (state.isError) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ErrorUi(message = state.errorMessage, onButtonClick = {
                            onRetry()
                        })
                    }
                } else if (state.isEmpty) {
                    EmptyList()
                } else {
                    ContentList(
                        reports = state.list.reports,
                        onItemClick = onEditReport,
                        onDownloadClick = { report ->
                            report.id?.let { onDownloadReport(it.toLong()) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ContentList(
    reports: List<Report>,
    onItemClick: (Report) -> Unit = {},
    onDownloadClick: (Report) -> Unit = {}
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(reports) { report ->
            ReportItem(
                data = report,
                onItemClick = onItemClick,
                onDownloadClick = onDownloadClick
            )
        }
    }
}

@Composable
fun ReportItem(
    data: Report,
    modifier: Modifier = Modifier,
    onItemClick: (Report) -> Unit = {},
    onDownloadClick: (Report) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(data) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Report name and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Primary500)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Title with overflow handling
                BaseText(
                    // Default name to "Report" if name is null
                    text = "Report ${data.id}",
                    fontFamily = FontType.SEMI_BOLD,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Document icon
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Document",
                    tint = Primary500,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date information row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                    tint = Neutral300,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                BaseText(
                    text = data.date?.let { formatDate(it) } ?: "-",
                    fontFamily = FontType.REGULAR,
                    fontSize = 14.sp,
                    fontColor = Neutral300
                )
            }

            // Action row with download icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit button hint
                BaseText(
                    text = stringResource(R.string.tap_to_edit),
                    fontFamily = FontType.REGULAR,
                    fontSize = 12.sp,
                    fontColor = Neutral300,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Download button
                IconButton(
                    onClick = { onDownloadClick(data) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = stringResource(R.string.download_report),
                        tint = Primary500,
                    )
                }
            }
        }
    }
}
