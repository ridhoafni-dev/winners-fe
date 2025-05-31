package com.dailyapps.feature.activty_plan.page.detail

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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dailyapps.activity_plan.R
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Primary600
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.ErrorUi
import com.dailyapps.common.components.FontType
import com.dailyapps.common.components.LoadingUi
import com.dailyapps.common.utils.DateTime.Companion.formatDate
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.entity.Teacher
import com.dailyapps.feature.activty_plan.ActivityPlanViewModel
import com.dailyapps.feature.activty_plan.state.ActivityPlanAction
import com.dailyapps.feature.activty_plan.state.ActivityPlanState
import com.dailyapps.common.R as commonR

@Composable
fun ActivityPlanDetailScreen(
    navController: NavHostController,
    activityPlanId: Long,
    viewModel: ActivityPlanViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    // Track if review dialog should be shown
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentState.token) {
        if (currentState.token.isEmpty()) return@LaunchedEffect

        viewModel.handleAction(
            ActivityPlanAction.OnGetActivityPlan(activityPlanId, currentState.token)
        )
    }

    // Check if the current user is a lecturer
    val isUserLecturer = remember(currentState.role) {
        currentState.isUserLecturer
    }

    // Check if activity plan already has a comment
    val hasExistingComment = remember(currentState.detail.activityPlan) {
        currentState.detail.activityPlan.activityPlanComment != null
    }

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.observation_detail_title),
                onClickBack = { navController.popBackStack() },
                // Only show menu icon if user is lecturer with no existing comment or if user is not lecturer
                menuIconResource = if (isUserLecturer && !hasExistingComment) commonR.drawable.ic_review
                                 else if (!isUserLecturer) commonR.drawable.ic_edit
                                 else null,
                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {
                    if (isUserLecturer && !hasExistingComment) {
                        showReviewDialog = true
                    } else if (!isUserLecturer) {
                        navController.navigate("${NavRoute.formActivityPlanScreen}/$activityPlanId")
                    }
                },
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (currentState.isLoading) {
                // Center the loading indicator both horizontally and vertically
                LoadingUi(modifier = Modifier.align(Alignment.Center))
            }
            else if (currentState.isError) {
                // Center the error message both horizontally and vertically
                ErrorUi(
                    message = currentState.errorMessage,
                    onButtonClick = {
                        viewModel.handleAction(
                            ActivityPlanAction.OnGetActivityPlan(activityPlanId, currentState.token)
                        )
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    DetailContent(
                        activityPlan = currentState.detail.activityPlan,
                        lecturers = currentState.add.lecturers
                    )
                }
            }
        }
    }

    // Show review dialog if needed
    if (showReviewDialog) {
        ReviewDialog(
            activityPlanId = activityPlanId,
            viewModel = viewModel,
            state = currentState,
            onDismiss = { showReviewDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDialog(
    activityPlanId: Long,
    viewModel: ActivityPlanViewModel,
    state: ActivityPlanState,
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
                ActivityPlanAction.OnGetActivityPlan(activityPlanId, state.token)
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
                                        ActivityPlanAction.OnSubmitReview(
                                            activityPlanId = activityPlanId,
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
fun DetailContent(
    activityPlan: ActivityPlan,
    lecturers: List<Teacher> = emptyList()
) {
    // Find lecturer name based on the activityPlanLecturer userId
    val lecturerId = activityPlan.activityPlanLecturer?.userId
    val lecturerName = if (lecturerId != null) {
        lecturers.find { it.id?.toString() == lecturerId.toString() }?.name ?: "Unknown Lecturer"
    } else {
        "Not Assigned"
    }

    // Status color based on status
    val statusColor = when(activityPlan.status?.lowercase()) {
        "aktif" -> Color(0xFF4CAF50)  // Green
        "reschedule" -> Color(0xFFFFA000)  // Amber
        "batal" -> Color(0xFFF44336)  // Red
        else -> Neutral300
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title and Status Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // Plan name with truncation
                BaseText(
                    text = activityPlan.name ?: "",
                    fontFamily = FontType.SEMI_BOLD,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status chip
                StatusChip(status = activityPlan.status ?: "Unknown", color = statusColor)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details in vertical table format
            VerticalDetailTable(
                items = listOf(
                    DetailTableItem(
                        label = "Lecturer",
                        value = lecturerName,
                        icon = Icons.Outlined.Person
                    ),
                    DetailTableItem(
                        label = "Start Date",
                        value = activityPlan.startDate?.let { formatDateTime(it) } ?: "-",
                        icon = Icons.Outlined.CalendarToday
                    ),
                    DetailTableItem(
                        label = "End Date",
                        value = activityPlan.endDate?.let { formatDateTime(it) } ?: "-",
                        icon = Icons.Outlined.Event
                    ),
                    DetailTableItem(
                        label = "Created By",
                        value = activityPlan.user?.email ?: "-",
                        icon = Icons.Outlined.AccountCircle
                    ),
                    DetailTableItem(
                        label = "Created At",
                        value = activityPlan.createAt?.let { formatDate(it) } ?: "-",
                        icon = Icons.Outlined.AccessTime
                    ),
                )
            )

            // Comment section - only displayed if activityPlanComment exists
            activityPlan.activityPlanComment?.let { comment ->
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

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
                        Surface(
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
fun StatusChip(status: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = color, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            BaseText(
                text = status,
                fontFamily = FontType.MEDIUM,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                fontColor = color
            )
        }
    }
}

data class DetailTableItem(
    val label: String,
    val value: String,
    val icon: ImageVector? = null
)

@Composable
fun VerticalDetailTable(items: List<DetailTableItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(1.dp)
    ) {
        items.forEachIndexed { index, item ->
            DetailTableRow(item = item)

            // Add divider except after last item
            if (index < items.size - 1) {
                Divider(
                    color = Color(0xFFEEEEEE),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun DetailTableRow(item: DetailTableItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon if available
        item.icon?.let {
            Icon(
                imageVector = it,
                contentDescription = item.label,
                tint = Neutral300,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        // Label and value
        Column {
            BaseText(
                text = item.label,
                fontFamily = FontType.MEDIUM,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                fontColor = Neutral300
            )

            Spacer(modifier = Modifier.height(2.dp))

            BaseText(
                text = item.value,
                fontFamily = FontType.REGULAR,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
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
