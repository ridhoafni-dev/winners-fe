package com.dailyapps.feature.self_reflection.page.detail

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
import androidx.compose.material.icons.outlined.Comment
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
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Primary500
import com.dailyapps.common.Primary600
import com.dailyapps.common.White
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.ErrorUi
import com.dailyapps.common.components.FontType
import com.dailyapps.common.components.LoadingUi
import com.dailyapps.common.utils.DateTime.Companion.formatDate
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.entity.SelfReflection
import com.dailyapps.feature.selfreflection.SelfReflectionViewModel
import com.dailyapps.feature.selfreflection.state.SelfReflectionAction

@Composable
fun SelfReflectionDetailScreen(
    navController: NavHostController,
    selfReflectionId: Long,
    viewModel: SelfReflectionViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    // Track if review dialog should be shown
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentState.token) {
        if (currentState.token.isEmpty()) return@LaunchedEffect

        viewModel.handleAction(
            SelfReflectionAction.LoadSelfReflection(selfReflectionId)
        )
    }

    // Check if the current user is a lecturer
    val isUserLecturer = remember(currentState.role) {
        currentState.isUserLecturer
    }

    // Check if self reflection already has a comment
    val hasExistingComment = remember(currentState.detail.selfReflection) {
        currentState.detail.selfReflection?.selfReflectionComment != null
    }

    Scaffold(
        topBar = {
            BaseAppBar(
                title = "Self-Reflection Detail", onClickBack = { navController.popBackStack() },
                // Only show edit icon for regular users or review icon for lecturers without comments
                menuIconResource = if (isUserLecturer && !hasExistingComment) com.dailyapps.common.R.drawable.ic_review
                else if (!isUserLecturer) com.dailyapps.common.R.drawable.ic_edit
                else null, elevation = 1.dp, modifier = modifier, onMenuClick = {
                    if (isUserLecturer && !hasExistingComment) {
                        showReviewDialog = true
                    } else if (!isUserLecturer) {
                        navController.navigate("${NavRoute.selfReflectionFormScreen}/$selfReflectionId")
                    }
                })
        }, containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (currentState.detail.isLoading) {
                LoadingUi(modifier = Modifier.align(Alignment.Center))
            } else if (currentState.detail.errorMessage != null) {
                ErrorUi(
                    message = currentState.detail.errorMessage, onButtonClick = {
                        viewModel.handleAction(
                            SelfReflectionAction.LoadSelfReflection(selfReflectionId)
                        )
                    }, modifier = Modifier.align(Alignment.Center)
                )
            } else {
                currentState.detail.selfReflection?.let { selfReflection ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        SelfReflectionDetailContent(
                            selfReflection = selfReflection,
                            isLecturer = isUserLecturer,
                            onAddCommentClick = { showReviewDialog = true })
                    }
                } ?: run {
                    // Show empty state when self reflection is null
                    Box(modifier = Modifier.fillMaxSize()) {
                        BaseText(
                            text = "Self Reflection not found",
                            fontFamily = FontType.MEDIUM,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

    // Show review dialog if needed
    if (showReviewDialog) {
        ReviewDialog(
            selfReflectionId = selfReflectionId,
            viewModel = viewModel,
            state = currentState,
            onDismiss = {
                showReviewDialog = false
            })
    }
}

@Composable
fun SelfReflectionDetailContent(
    selfReflection: SelfReflection,
    isLecturer: Boolean,
    onAddCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Self-Reflection Title and Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                BaseText(
                    text = selfReflection.title ?: "-",
                    fontSize = 18.sp,
                    fontFamily = FontType.SEMI_BOLD,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Created Date
                InfoRow(
                    icon = Icons.Outlined.AccessTime,
                    label = "Created on",
                    value = selfReflection.createAt ?: "-"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Student Info
                InfoRow(
                    icon = Icons.Outlined.Person,
                    label = "Student",
                    value = selfReflection.user?.name ?: "-"
                )

                // If lecturer is assigned, show their info
                selfReflection.selfReflectionLecturer?.name?.let { lecturerName ->
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(
                        icon = Icons.Outlined.Person, label = "Lecturer", value = lecturerName
                    )
                }
            }
        }
    }

    // Comments Section Title
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseText(
            text = "Comments", fontSize = 16.sp, fontFamily = FontType.SEMI_BOLD
        )

        // Add comment button for lecturers
        if (isLecturer) {
            Button(
                onClick = onAddCommentClick, colors = ButtonDefaults.buttonColors(
                    containerColor = Primary500
                ), shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Comment,
                    contentDescription = "Add Comment",
                    tint = White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add Comment", color = White, fontSize = 14.sp
                )
            }
        }
    }

    // Comment section - only displayed if memoComment exists
    selfReflection.selfReflectionComment?.let { comment ->
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

@Composable
fun RatingBar(
    rating: Int, modifier: Modifier = Modifier, maxRating: Int = 5
) {
    Row(modifier = modifier) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (i <= rating) Color(0xFFFFC107) else Neutral300,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary500,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label:", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDialog(
    selfReflectionId: Long,
    viewModel: SelfReflectionViewModel,
    state: com.dailyapps.feature.selfreflection.state.SelfReflectionState,
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
            // Refetch self reflection details to show the new comment
            viewModel.handleAction(
                SelfReflectionAction.LoadSelfReflection(selfReflectionId)
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
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dialog Title - Keep outside of scroll area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary600.copy(alpha = 0.05f))
                        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BaseText(
                        text = "Review Self-Reflection",
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
                                    })
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
                        value = rating.toFloat(), onValueChange = {
                            rating = it.toInt()
                            // Clear error when user selects a valid rating
                            if (rating > 0) {
                                ratingError = ""
                            }
                        }, valueRange = 0f..5f, steps = 4, colors = SliderDefaults.colors(
                            thumbColor = Primary600,
                            activeTrackColor = Primary600,
                            inactiveTrackColor = Neutral300
                        ), modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
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
                                    text = commentError, color = Color.Red, fontSize = 12.sp
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
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
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
                                        SelfReflectionAction.AddComment(
                                            reflectionId = selfReflectionId,
                                            userId = state.userId,
                                            rating = rating,
                                            comment = comment,
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
