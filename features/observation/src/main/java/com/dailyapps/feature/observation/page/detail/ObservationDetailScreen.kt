package com.dailyapps.feature.observation.page.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.dailyapps.common.Neutral100
import com.dailyapps.common.Neutral300
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.ErrorUi
import com.dailyapps.common.components.FontType
import com.dailyapps.common.components.LoadingUi
import com.dailyapps.common.utils.DateTime.Companion.formatDate
import com.dailyapps.common.utils.httpFormat
import com.dailyapps.entity.Observation
import com.dailyapps.feature.observation.ObservationViewModel
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.observation.R

@Composable
fun ObservationDetailScreen(
    navController: NavHostController,
    observationId: Long,
    viewModel: ObservationViewModel,
    modifier: Modifier = Modifier
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    LaunchedEffect(currentState.token) {
        if (currentState.token.isEmpty()) return@LaunchedEffect

        viewModel.handleAction(
            ObservationAction.OnGetObservation(observationId, currentState.token)
        )
    }
    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.title_observation_detail),
                onClickBack = { navController.popBackStack() },
                menuIconResource = com.dailyapps.common.R.drawable.ic_history,
                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {},
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            if (currentState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LoadingUi(modifier = Modifier.align(Alignment.Center))
                }
            }
            else if (currentState.isError) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ErrorUi(message = currentState.errorMessage, onButtonClick = {
                    })
                }
            }
            else {
                DetailContent(currentState.detail.observation)
            }
        }
    }
}

@Composable
fun DetailContent(observation: Observation) {
// Image section
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Neutral100)
    ) {
        observation.image?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = imageUrl.httpFormat())
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                            placeholder(R.drawable.placeholder_image)
                            error(R.drawable.error_image)
                        }).build()
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // Content section
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title
        BaseText(
            text = observation.name ?: "",
            fontFamily = FontType.MEDIUM,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        // Date and creator info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BaseText(
                text = observation.createAt?.let {
                    formatDate(it)
                } ?: "-",
                fontColor = Neutral300,
                fontSize = 14.sp
            )

            BaseText(
                text = "By: ${observation.user?.email ?: "-"}",
                fontColor = Neutral300,
                fontSize = 14.sp
            )
        }

        // Description
        BaseText(
            text = observation.description ?: "",
            fontColor = Neutral300,
            modifier = Modifier.padding(top = 16.dp),
            lineHeight = 24.sp
        )
    }
}
