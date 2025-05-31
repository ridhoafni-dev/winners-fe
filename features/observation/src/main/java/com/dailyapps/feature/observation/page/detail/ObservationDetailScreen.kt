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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.common.utils.httpFormat
import com.dailyapps.entity.Observation
import com.dailyapps.entity.User
import com.dailyapps.feature.observation.ObservationViewModel
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.common.R as commonR
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
                menuIconResource = commonR.drawable.ic_edit,
                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {
                    navController.navigate("${NavRoute.formObservationScreen}/$observationId")
                },
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

        // Comments section - only displayed if comments exist
        observation.observationComments?.let { comment ->
            androidx.compose.material3.Divider(
                color = Neutral100,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material.icons.Icons.Outlined.Comment?.let {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Comment,
                            contentDescription = "Comments",
                            tint = androidx.compose.ui.graphics.Color(0xFFFFB74D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    BaseText(
                        text = "Comments",
                        fontFamily = FontType.SEMI_BOLD,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

                // Comment text
                comment.comment?.let { commentText ->
                    androidx.compose.material3.Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        color = androidx.compose.ui.graphics.Color(0xFFFFFDE7)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            BaseText(
                                text = commentText,
                                fontFamily = FontType.REGULAR,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )

                            // Rating display if available
                            comment.rating?.let { rating ->
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BaseText(
                                        text = "Rating:",
                                        fontFamily = FontType.MEDIUM,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        fontColor = Neutral300
                                    )
                                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))

                                    RatingBar(rating = rating)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
