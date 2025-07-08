package com.dailyapps.feature.memo.page.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dailyapps.common.Neutral300
import com.dailyapps.common.Primary500
import com.dailyapps.common.White
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.ErrorUi
import com.dailyapps.common.components.FontType
import com.dailyapps.common.components.LoadingUi
import com.dailyapps.common.utils.DateTime.Companion.formatDate
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.entity.Memo
import com.dailyapps.feature.memo.MemoViewModel
import com.dailyapps.feature.memo.R
import com.dailyapps.feature.memo.state.MemoAction

@Composable
fun MemoDetailScreen(
    navController: NavHostController,
    memoId: Long,
    viewModel: MemoViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    LaunchedEffect(currentState.token) {
        if (currentState.token.isEmpty()) return@LaunchedEffect

        viewModel.handleAction(
            MemoAction.LoadMemo(memoId)
        )
    }

    // Check if the current user is a lecturer
    val isUserLecturer = remember(currentState.role) {
        currentState.isUserLecturer
    }

    Scaffold(
        topBar = {
            BaseAppBar(
                title = stringResource(R.string.memo_detail_title),
                onClickBack = { navController.popBackStack() },
                // Only show edit icon if user is not a lecturer
                menuIconResource = if (!isUserLecturer) com.dailyapps.common.R.drawable.ic_edit else null,
                elevation = 1.dp,
                modifier = modifier,
                onMenuClick = {
                    if (!isUserLecturer) {
                        navController.navigate("${NavRoute.formMemoScreen}/$memoId")
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
            if (currentState.memoDetailState.isLoading) {
                LoadingUi(modifier = Modifier.align(Alignment.Center))
            }
            else if (currentState.memoDetailState.errorMessage != null) {
                ErrorUi(
                    message = currentState.memoDetailState.errorMessage,
                    onButtonClick = {
                        viewModel.handleAction(
                            MemoAction.LoadMemo(memoId)
                        )
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else {
                currentState.memoDetailState.memo?.let { memo ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        MemoDetailContent(memo = memo)
                    }
                } ?: run {
                    // Show empty state when memo is null
                    Box(modifier = Modifier.fillMaxSize()) {
                        BaseText(
                            text = stringResource(R.string.memo_not_found),
                            fontFamily = FontType.MEDIUM,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemoDetailContent(
    memo: Memo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title section
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
                    text = memo.title ?: stringResource(R.string.no_title),
                    fontFamily = FontType.SEMI_BOLD,
                    fontSize = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Memo Details Section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Created by information
                InfoRow(
                    icon = Icons.Outlined.Person,
                    label = stringResource(R.string.created_by),
                    value = memo.user?.name ?: stringResource(R.string.unknown_user)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Creation date information
                InfoRow(
                    icon = Icons.Outlined.AccessTime,
                    label = stringResource(R.string.created_date),
                    value = memo.createAt?.let { formatDate(it) } ?: stringResource(R.string.unknown_date)
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Neutral300,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Neutral300
            )

            BaseText(
                text = value,
                fontFamily = FontType.MEDIUM,
                fontSize = 16.sp,
            )
        }
    }
}
