package com.dailyapps.feature.observation.page.list

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dailyapps.common.EmptyList
import com.dailyapps.common.Neutral100
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Primary
import com.dailyapps.common.White
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.ErrorUi
import com.dailyapps.common.components.LoadingUi
import com.dailyapps.common.components.TextFieldDropdown
import com.dailyapps.common.utils.DateTime
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.entity.Observation
import com.dailyapps.feature.observation.ObservationViewModel
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.feature.observation.state.ObservationState
import com.dailyapps.observation.R

@Composable
fun ObservationListScreen(
    navController: NavHostController,
    viewModel: ObservationViewModel,
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    LaunchedEffect(currentState.startDate, currentState.endDate, currentState.token) {
        if (currentState.token.isEmpty() || currentState.isUserNotExist) return@LaunchedEffect

        viewModel.handleAction(
            ObservationAction.OnGetObservations(
                userId = currentState.userId,
                startDate = currentState.startDate,
                endDate = currentState.endDate,
                token = currentState.token
            )
        )
    }

    ObservationListContent(
        navController = navController,
        state = state.value,
    )
}

@Composable
fun ObservationListContent(navController: NavHostController, state: ObservationState) {
    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.title_observation),
                onClickBack = { navController.popBackStack() },
                menuIconResource = com.dailyapps.common.R.drawable.ic_history,
                elevation = 1.dp
            ) {
                Handler(Looper.getMainLooper()).postDelayed({
                    navController.navigate(NavRoute.noteScreen)
                }, 200)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Column {
                TextFieldDropdown(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Date",
                    label = "Date",
                    itemsDropdown = emptyList(),
                    onValueChange = { value ->
                    }
                )

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoadingUi(modifier = Modifier.align(Alignment.Center))
                    }
                }
                else if (state.isError) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ErrorUi(message = state.errorMessage, onButtonClick = {
                        })
                    }
                }
                else if (state.isEmpty) {
                    EmptyList()
                }
                else {
                    ContentList(state.observations)
                }
            }
        }
    }
}

@Composable
fun ContentList(observations: List<Observation>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(observations) { observation ->
            ItemObservation(data = observation)
        }
    }
}

    @SuppressLint("NewApi")
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
                .clickable { onItemClick(data) },
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Column(
                    modifier = modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    BaseText(
                        text = data.createAt?.let {
                            DateTime.getIndoDayOfWeek(it)
                        } ?: "",
                        fontColor = Neutral300
                    )
//                    BaseText(
//                        text = data.tanggalAbsensi?.let {
//                            "${DateTime.convertToShort(date = it)}, ${absent.jamMasuk}"
//                        } ?: "",
//                        fontFamily = FontType.MEDIUM,
//                        fontWeight = FontWeight.SemiBold,
//                        fontSize = 20.sp,
//                        modifier = modifier.padding(top = 8.dp)
//                    )
                }
                Box(
                    modifier = modifier
                        .background(color = Primary, shape = RoundedCornerShape(8.dp)),

                    ) {
                    data.name?.uppercase()
                        ?.let {
                            BaseText(
                                text = it,
                                fontColor = White,
                                modifier = modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                            )
                        }
                }
            }
        }
    }
