package com.dailyapps.feature.activty_plan.page.list

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dailyapps.activity_plan.R
import com.dailyapps.common.Danger600
import com.dailyapps.common.EmptyList
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Primary500
import com.dailyapps.common.Success500
import com.dailyapps.common.Warning500
import com.dailyapps.common.White
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.DateRangeFilter
import com.dailyapps.common.components.ErrorUi
import com.dailyapps.common.components.FontType
import com.dailyapps.common.components.LoadingUi
import com.dailyapps.common.utils.DateTime.Companion.formatDate
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.feature.activty_plan.ActivityPlanViewModel
import com.dailyapps.feature.activty_plan.state.ActivityPlanAction
import com.dailyapps.feature.activty_plan.state.ActivityPlanState
import com.dailyapps.common.R as commonR

@Composable
fun ActivityPlanListScreen(
    navController: NavHostController,
    viewModel: ActivityPlanViewModel,
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    // Check if the current user is a lecturer
    val isUserLecturer = remember(currentState.role) {
        currentState.isUserLecturer
    }

    LaunchedEffect(currentState.list.startDate, currentState.list.endDate, currentState.token) {
        if (currentState.token.isEmpty() || currentState.isUserNotExist) return@LaunchedEffect

        viewModel.handleAction(
            ActivityPlanAction.OnGetActivityPlans(
                userId = currentState.userId,
                startDate = currentState.list.startDate,
                endDate = currentState.list.endDate,
                token = currentState.token
            )
        )
    }

    val onRetry: () -> Unit = {
        viewModel.handleAction(
            ActivityPlanAction.OnGetActivityPlans(
                userId = currentState.list.userId,
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
        onDatePickerChange = { startDate, endDate ->
            viewModel.handleAction(
                ActivityPlanAction.OnUpdateDateRange(
                    startDate = startDate,
                    endDate = endDate,
                )
            )
            viewModel.handleAction(
                ActivityPlanAction.OnGetActivityPlans(
                    userId = currentState.list.userId,
                    startDate = startDate,
                    endDate = endDate,
                    token = currentState.token
                )
            )
        },
        onRetry = onRetry
    )
}

@Composable
fun ListContent(
    navController: NavHostController,
    state: ActivityPlanState,
    isUserLecturer: Boolean = false,
    onDatePickerChange: (String, String) -> Unit,
    onRetry: () -> Unit = {}
) {

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.activity_plan_title),
                onClickBack = { navController.popBackStack() },
                // Only show menu icon (add button) if user is not a lecturer
                menuIconResource = if (!isUserLecturer) commonR.drawable.ic_add else null,
                elevation = 1.dp,
                onMenuClick = if (!isUserLecturer) {
                    { navController.navigate("${NavRoute.formActivityPlanScreen}/0") }
                } else {
                    {}
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Column {
                // Existing content
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
                }
                else if (state.isError) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ErrorUi(message = state.errorMessage, onButtonClick = {
                            onRetry()
                        })
                    }
                }
                else if (state.isEmpty) {
                    EmptyList()
                }
                else {
                    ContentList(state.list.activityPlans, onItemClick = { activityPlan ->
                        navController.navigate("${NavRoute.activityPlanDetailScreen}/${activityPlan.id ?: 0}") {
                            launchSingleTop = true
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun ContentList(activityPlans: List<ActivityPlan>, onItemClick: (ActivityPlan) -> Unit = {}) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(activityPlans) { activityPlan ->
            ActivityPlanItem(data = activityPlan) { activityPlanParam ->
                onItemClick(activityPlanParam)
            }
        }
    }
}

@Composable
fun ActivityPlanItem(
    data: ActivityPlan,
    modifier: Modifier = Modifier,
    onItemClick: (ActivityPlan) -> Unit = {},
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
            // Status indicator and title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status indicator
                val statusColor = when (data.status?.lowercase()) {
                    "aktif" -> Success500
                    "reschedule" -> Warning500
                    "batal" -> Danger600
                    else -> Primary500
                }
                
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Title with overflow handling
                BaseText(
                    text = data.name ?: "-",
                    fontFamily = FontType.SEMI_BOLD,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                
                // Status text
                BaseText(
                    text = data.status ?: "Unknown",
                    fontFamily = FontType.MEDIUM,
                    fontSize = 14.sp,
                    fontColor = statusColor,
                    modifier = Modifier.padding(start = 8.dp)
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
                    contentDescription = "Date Range",
                    tint = Neutral300,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                BaseText(
                    text = "${data.startDate?.let { formatDate(it) } ?: "-"} to ${data.endDate?.let { formatDate(it) } ?: "-"}",
                    fontFamily = FontType.REGULAR,
                    fontSize = 14.sp,
                    fontColor = Neutral300
                )
            }
        }
    }
}
