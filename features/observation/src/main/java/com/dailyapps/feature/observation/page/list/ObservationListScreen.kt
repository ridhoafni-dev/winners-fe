package com.dailyapps.feature.observation.page.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.dailyapps.common.EmptyList
import com.dailyapps.common.Neutral100
import com.dailyapps.common.Neutral300
import com.dailyapps.common.White
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.DateRangeFilter
import com.dailyapps.common.components.ErrorUi
import com.dailyapps.common.components.FontType
import com.dailyapps.common.components.LoadingUi
import com.dailyapps.common.utils.DateTime.Companion.formatDate
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.common.utils.httpFormat
import com.dailyapps.entity.Observation
import com.dailyapps.feature.observation.ObservationViewModel
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.feature.observation.state.ObservationState
import com.dailyapps.observation.R
import com.dailyapps.common.R as commonR

@Composable
fun ObservationListScreen(
    navController: NavHostController,
    viewModel: ObservationViewModel,
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
            ObservationAction.OnGetObservations(
                userId = currentState.userId,
                startDate = currentState.list.startDate,
                endDate = currentState.list.endDate,
                token = currentState.token
            )
        )
    }

    ObservationListContent(
        navController = navController,
        state = state.value,
        isUserLecturer = isUserLecturer,
        onDatePickerChange = { startDate, endDate ->
            viewModel.handleAction(
                ObservationAction.OnUpdateDateRange(
                    startDate = startDate,
                    endDate = endDate,
                )
            )
            viewModel.handleAction(
                ObservationAction.OnGetObservations(
                    userId = currentState.list.userId,
                    startDate = startDate,
                    endDate = endDate,
                    token = currentState.token
                )
            )
        }
    )
}

@Composable
fun ObservationListContent(
    navController: NavHostController,
    state: ObservationState,
    onDatePickerChange: (String, String) -> Unit,
    isUserLecturer: Boolean = false,
) {

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.title_observation),
                onClickBack = { navController.popBackStack() },
                menuIconResource = if (!isUserLecturer) commonR.drawable.ic_add else null,
                elevation = 1.dp
            ) {
                if (!isUserLecturer) {
                    navController.navigate("${NavRoute.formObservationScreen}/0")
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
                        ErrorUi(message = state.errorMessage, onButtonClick = {})
                    }
                }
                else if (state.isEmpty) {
                    EmptyList()
                }
                else {
                    ContentList(state.list.observations, onItemClick = { observation ->
                        navController.navigate("${NavRoute.observationDetailScreen}/${observation.id ?: 0}") {
                            launchSingleTop = true
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun ContentList(observations: List<Observation>, onItemClick: (Observation) -> Unit = {}) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(observations) { observation ->
            ItemObservation(data = observation) { observationParam ->
                onItemClick(observationParam)
            }
        }
    }
}

@Composable
fun ItemObservation(
    data: Observation,
    modifier: Modifier = Modifier,
    onItemClick: (Observation) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Neutral100, shape = RoundedCornerShape(8.dp))
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable { onItemClick(data) }
            .background(White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image on the left with rounded corners
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Neutral100)
            ) {
                // If using Coil for image loading
                data.image?.let { imageUrl ->
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current).data(data = imageUrl.httpFormat())
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                    placeholder(commonR.drawable.placeholder_image)
                                    error(commonR.drawable.error_image)
                                }).build()
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Texts on the right
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                // Date text
                BaseText(
                    text = data.createAt?.let {
                        formatDate(it)
                    } ?: "-",
                    fontColor = Neutral300
                )

                // Main text
                BaseText(
                    text = data.name ?: data.description ?: "",
                    fontFamily = FontType.MEDIUM,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
