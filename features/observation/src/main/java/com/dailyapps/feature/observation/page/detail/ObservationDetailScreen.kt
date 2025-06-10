package com.dailyapps.feature.observation.page.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.dailyapps.common.Neutral100
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Primary600
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
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

    var showReviewDialog by remember { mutableStateOf(false) }

    // Check if the current user is a lecturer
    val isUserLecturer = remember(currentState.role) {
        currentState.isUserLecturer
    }

    // Check if activity plan already has a comment
    val hasExistingComment = remember(currentState.detail.observation) {
        currentState.detail.observation.observationComments != null
    }

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.title_observation_detail),
                onClickBack = { navController.popBackStack() },
                menuIconResource = if (isUserLecturer && !hasExistingComment) commonR.drawable.ic_review
                else if (!isUserLecturer) commonR.drawable.ic_edit
                else null,                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {
                    if (isUserLecturer && !hasExistingComment) {
                        showReviewDialog = true
                    } else if (!isUserLecturer) {
                        navController.navigate("${NavRoute.formObservationScreen}/$observationId")
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(
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

    // Show review dialog if needed
    if (showReviewDialog) {
        ReviewDialog(
            observationId = observationId,
            viewModel = viewModel,
            state = currentState,
            onDismiss = { showReviewDialog = false }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDialog(
    observationId: Long,
    viewModel: ObservationViewModel,
    state: ObservationState,
    onDismiss: () -> Unit
) {
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

    // Validation state
    var ratingError by remember { mutableStateOf("") }
    var commentError by remember { mutableStateOf("") }
    var isSubmitAttempted by remember { mutableStateOf(false) }

    // Track if submission was successful
    val isLoading = state.isLoading
    val isSuccess = state.isSuccess

    // Refresh data when submission is successful
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            // Refetch activity plan details to show the new comment
            viewModel.handleAction(
                ObservationAction.OnGetObservation(observationId, state.token)
            )
            onDismiss()
        }
    }

    // Validate function
    fun validateForm(): Boolean {
        isSubmitAttempted = true

        // Validate rating
        ratingError = if (rating <= 0) {
            "Rating is required"
        } else {
            ""
        }

        // Validate comment
        commentError = if (comment.isBlank()) {
            "Comment is required"
        } else if (comment.length < 5) {
            "Comment must be at least 5 characters"
        } else {
            ""
        }

        return ratingError.isEmpty() && commentError.isEmpty()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp), // Set maximum height
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Dialog Title - Keep outside of scroll area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary600.copy(alpha = 0.05f))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BaseText(
                        text = "Review Activity",
                        fontFamily = FontType.SEMI_BOLD,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }

                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Rating Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BaseText(
                            text = "Rating",
                            fontFamily = FontType.MEDIUM,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                        )

                        BaseText(
                            text = "$rating/5",
                            fontFamily = FontType.MEDIUM,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            fontColor = if (rating > 0) Primary600 else Neutral300
                        )
                    }

                    // Star Rating Display with clickable stars
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        repeat(5) { index ->
                            val starPosition = index + 1
                            Icon(
                                imageVector = if (starPosition <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Rating $starPosition",
                                tint = if (starPosition <= rating) Color(0xFFFFC107) else Neutral300,
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    // Make stars clickable for easier rating selection
                                    .background(Color.Transparent)
                                    .clickable {
                                        rating = starPosition
                                        if (rating > 0) {
                                            ratingError = ""
                                        }
                                    }
                            )
                        }
                    }

                    // Error message for rating
                    if (isSubmitAttempted && ratingError.isNotEmpty()) {
                        Text(
                            text = ratingError,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    // Rating Slider
                    Slider(
                        value = rating.toFloat(),
                        onValueChange = {
                            rating = it.toInt()
                            // Clear error when user selects a valid rating
                            if (rating > 0) {
                                ratingError = ""
                            }
                        },
                        valueRange = 0f..5f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = Primary600,
                            activeTrackColor = Primary600,
                            inactiveTrackColor = Neutral300
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )

                    // Comment Input - now with error state
                    OutlinedTextField(
                        value = comment,
                        onValueChange = {
                            comment = it
                            // Clear error when user starts typing a valid comment
                            if (comment.length >= 5) {
                                commentError = ""
                            }
                        },
                        label = { Text("Comment") },
                        placeholder = { Text("Enter your feedback here (required)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(120.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = if (isSubmitAttempted && commentError.isNotEmpty()) Color.Red else Primary600,
                            unfocusedBorderColor = if (isSubmitAttempted && commentError.isNotEmpty()) Color.Red else Neutral300
                        ),
                        isError = isSubmitAttempted && commentError.isNotEmpty(),
                        supportingText = {
                            if (isSubmitAttempted && commentError.isNotEmpty()) {
                                Text(
                                    text = commentError,
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Action Buttons - Keep outside of scroll area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            enabled = !isLoading
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                // Validate form before submitting
                                if (validateForm()) {
                                    // Submit review logic here
                                    viewModel.handleAction(
                                        ObservationAction.OnSubmitReview(
                                            observationId = observationId,
                                            userId = state.userId,
                                            rating = rating,
                                            comment = comment
                                        )
                                    )
                                    // Don't dismiss here - wait for success in LaunchedEffect
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary600),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                androidx.compose.material.CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submitting...")
                            } else {
                                Text("Submit")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailContent(observation: Observation) {
// Image section
    Column {
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
                        Icon(
                            imageVector = Icons.Outlined.Comment,

                            contentDescription = "Comments",
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BaseText(
                            text = "Comments",
                            fontFamily = FontType.SEMI_BOLD,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Comment text
                    comment.comment?.let { commentText ->
                        androidx.compose.material3.Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFFDE7)
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
                                    Spacer(modifier = Modifier.height(8.dp))
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
                                        Spacer(modifier = Modifier.width(4.dp))

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
}

@Composable
fun RatingBar(rating: Int, maxRating: Int = 5) {
    Row {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC107) else Neutral300,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 2.dp)
            )
        }
    }
}

// Helper function to format datetime strings
fun formatDateTime(dateTimeString: String): String {
    return try {
        // Assuming the dateTime is in a standard format
        val parts = dateTimeString.split("T")
        if (parts.size >= 2) {
            val date = formatDate(parts[0])
            // Extract only hours and minutes from the time part
            val timeString = parts[1]
            val timeParts = timeString.split(":")
            val formattedTime = if (timeParts.size >= 2) {
                "${timeParts[0]}:${timeParts[1]}"  // Display only HH:MM
            } else {
                timeString.substringBefore(".")  // Fallback to original format
            }
            "$date, $formattedTime"
        } else {
            formatDate(dateTimeString)
        }
    } catch (e: Exception) {
        dateTimeString
    }
}

