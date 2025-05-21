package com.dailyapps.home.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dailyapps.common.BgProfile
import com.dailyapps.common.Divider2
import com.dailyapps.common.Neutral900
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseDialog
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.FontType
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.common.utils.formattedAppVersion
import com.dailyapps.home.HomeViewModel
import com.dailyapps.home.R

@Composable
fun ProfileScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {

    val student by homeViewModel.student.collectAsState()

    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://www.privacypolicygenerator.org/live.php?token=dCwCqDKla97LpVIgiB4YF5qwJ4kwg6Cs")) }
    var signOutDialogState by remember { mutableStateOf(false) }

    val showSignOutDialog: () -> Unit = {
        signOutDialogState = true
    }

    val hideSignOutDialog: () -> Unit = {
        signOutDialogState = false
    }

    val onSignOutConfirm: () -> Unit = {
        hideSignOutDialog()
        homeViewModel.logout()
    }

    LaunchedEffect(key1 = Unit) {
        homeViewModel.getUser()
        //homeViewModel.getStudent()
    }

    if (signOutDialogState) {
        BaseDialog(
            title = "Apakah kamu yakin ingin melakukan signout?",
            description = "Ketika kamu melakukan signout kamu tidak dapat mengakses menu didalam aplikasi, dan harus melakukan login kembali!",
            onConfirmClicked = onSignOutConfirm,
            confirmButtonText = "Signout",
            cancelButtonText = "Batal",
            onCancelRequest = hideSignOutDialog
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BaseAppBar(
                title = "Profile",
                modifier = Modifier.fillMaxWidth(),
                onClickBack = { navController.popBackStack() },
                onMenuClick = {})
        },
    ) { contentPadding ->
        Column {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .weight(1f)
            ) {
                item {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        val (profile, name, email, nisn, password, _, _) = createRefs()
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(if (student.foto.isNullOrEmpty()) "https://bilpunkten.se/wp-content/uploads/2021/03/dummy-user-image-e1616512544203-1.png" else student.foto)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.image_profile),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .constrainAs(profile) {
                                    top.linkTo(parent.top, 32.dp)
                                    start.linkTo(parent.start, 24.dp)
                                    end.linkTo(parent.end, 24.dp)
                                }
                        )

                        Column(
                            modifier = Modifier
                                .constrainAs(nisn) {
                                    top.linkTo(profile.bottom, 16.dp)
                                    start.linkTo(parent.start, 24.dp)
                                    end.linkTo(parent.end, 16.dp)
                                    width = Dimension.fillToConstraints
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BaseText(
                                text = student.nama.toString(),
                                fontFamily = FontType.REGULAR,
                            )
                            Divider(color = Divider2, modifier = Modifier
                                .height(1.dp)
                                .width(32.dp))
                            BaseText(
                                text = student.nisn.toString(),
                                fontFamily = FontType.LIGHT,
                                fontSize = 12.sp,
                                fontColor = Neutral900
                            )
                        }

                        Row(
                            modifier = Modifier
                                .constrainAs(name) {
                                    top.linkTo(nisn.bottom, 32.dp)
                                    start.linkTo(parent.start, 24.dp)
                                    end.linkTo(parent.end, 16.dp)
                                    width = Dimension.fillToConstraints
                                }
                                .clickable {
                                    navController.navigate(NavRoute.profileDetailScreen)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BaseText(
                                modifier = Modifier.weight(1F),
                                text = "Profile",
                                fontFamily = FontType.LIGHT,
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = BgProfile,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(painter = painterResource(com.dailyapps.common.R.drawable.ic_chevron_right), contentDescription = "Back")
                            }

                        }

                        Row(
                            modifier = Modifier
                                .constrainAs(password) {
                                    top.linkTo(name.bottom, 24.dp)
                                    start.linkTo(parent.start, 24.dp)
                                    end.linkTo(parent.end, 16.dp)
                                    width = Dimension.fillToConstraints
                                }
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navController.navigate(NavRoute.changePasswordScreen)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BaseText(
                                modifier = Modifier.weight(1F),
                                text = "Ganti Password",
                                fontFamily = FontType.LIGHT,
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = BgProfile,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(painter = painterResource(com.dailyapps.common.R.drawable.ic_chevron_right), contentDescription = "Back")
                            }

                        }

                        Row(
                            modifier = Modifier
                                .constrainAs(email) {
                                    top.linkTo(password.bottom, 24.dp)
                                    start.linkTo(parent.start, 24.dp)
                                    end.linkTo(parent.end, 16.dp)
                                    width = Dimension.fillToConstraints
                                }
                                .padding(vertical = 8.dp)
                                .clickable {
                                    context.startActivity(intent)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BaseText(
                                modifier = Modifier.weight(1F),
                                text = "Term & Condition",
                                fontFamily = FontType.LIGHT,
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = BgProfile,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(painter = painterResource(com.dailyapps.common.R.drawable.ic_chevron_right), contentDescription = "Back")
                            }

                        }

                        /*if(updateUserResponse is Resource.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .constrainAs(loading) {
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                    },
                                strokeWidth = 4.dp,
                                color = RedNetflix
                            )
                        }*/

                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showSignOutDialog() }
                        .background(color = BgProfile)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .width(150.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(com.dailyapps.common.R.drawable.ic_log_out), contentDescription = "Logout")
                    BaseText(
                        modifier = Modifier
                            .weight(1F)
                            .padding(start = 8.dp),
                        text = "Sign Out",
                        fontFamily = FontType.LIGHT,
                    )

                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BaseText(
                    text = formattedAppVersion,
                )
            }
        }

    }
}