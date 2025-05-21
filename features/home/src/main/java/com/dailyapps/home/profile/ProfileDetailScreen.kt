package com.dailyapps.home.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dailyapps.common.Neutral400
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseText
import com.dailyapps.common.components.FontType
import com.dailyapps.common.utils.uppercaseFirstChar
import com.dailyapps.home.HomeViewModel
import com.dailyapps.home.R

@Composable
fun ProfileDetailScreen(navController: NavController,
                        homeViewModel: HomeViewModel
) {

    val user by homeViewModel.user.collectAsState()
    val student by homeViewModel.student.collectAsState()

    LaunchedEffect(key1 = Unit) {
        homeViewModel.getUser()
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                ) {
                    val (profile, name, nameValue, email, emailValue, username, usernameValue, jk, jkValue, kelas, kelasValue, noHp, noHpValue, version, buttonLogout, loading) = createRefs()
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(if (student.foto.isNullOrEmpty()) "https://bilpunkten.se/wp-content/uploads/2021/03/dummy-user-image-e1616512544203-1.png" else student.foto)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(R.string.image_profile),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .border(
                                BorderStroke(2.dp, Neutral400),
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .constrainAs(profile) {
                                top.linkTo(parent.top, 32.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )

                    BaseText(
                        modifier = Modifier.constrainAs(name) {
                            top.linkTo(profile.bottom, 32.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = "Nama :",
                        fontFamily = FontType.LIGHT
                    )

                    BaseText(
                        modifier = Modifier.constrainAs(nameValue) {
                            top.linkTo(name.bottom, 4.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = student.nama ?: "".uppercaseFirstChar(),
                        fontSize = 18.sp,
                        fontFamily = FontType.SEMI_BOLD
                    )

                    BaseText(
                        modifier = Modifier.padding(top = 6.dp).constrainAs(email) {
                            top.linkTo(nameValue.bottom, 16.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = "Email :",
                        fontFamily = FontType.LIGHT
                    )

                    BaseText(
                        modifier = Modifier.constrainAs(emailValue) {
                            top.linkTo(email.bottom, 4.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = user?.email ?: "-",
                        fontSize = 18.sp,
                        fontFamily = FontType.SEMI_BOLD
                    )

                    BaseText(
                        modifier = Modifier.padding(top = 6.dp).constrainAs(username) {
                            top.linkTo(emailValue.bottom, 16.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = "NISN :",
                        fontFamily = FontType.LIGHT
                    )

                    BaseText(
                        modifier = Modifier.constrainAs(usernameValue) {
                            top.linkTo(username.bottom, 4.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = user?.email ?: "-",
                        fontSize = 18.sp,
                        fontFamily = FontType.SEMI_BOLD
                    )

                    BaseText(
                        modifier = Modifier.padding(top = 6.dp).constrainAs(jk) {
                            top.linkTo(usernameValue.bottom, 16.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = "JK :",
                        fontFamily = FontType.LIGHT
                    )

                    BaseText(
                        modifier = Modifier.constrainAs(jkValue) {
                            top.linkTo(jk.bottom, 4.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = student.jenisKelamin ?: "-",
                        fontSize = 18.sp,
                        fontFamily = FontType.SEMI_BOLD
                    )

                    BaseText(
                        modifier = Modifier.padding(top = 6.dp).constrainAs(kelas) {
                            top.linkTo(jkValue.bottom, 16.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = "Kelas :",
                        fontFamily = FontType.LIGHT
                    )

                    BaseText(
                        modifier = Modifier.constrainAs(kelasValue) {
                            top.linkTo(kelas.bottom, 4.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = student.namaKelas ?: "-",
                        fontSize = 18.sp,
                        fontFamily = FontType.SEMI_BOLD
                    )

                    BaseText(
                        modifier = Modifier.padding(top = 6.dp).constrainAs(noHp) {
                            top.linkTo(kelasValue.bottom, 16.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = "No HP :",
                        fontFamily = FontType.LIGHT
                    )

                    BaseText(
                        modifier = Modifier.constrainAs(noHpValue) {
                            top.linkTo(noHp.bottom, 4.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = student.noHp ?: "-",
                        fontSize = 18.sp,
                        fontFamily = FontType.SEMI_BOLD
                    )

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
    }
}