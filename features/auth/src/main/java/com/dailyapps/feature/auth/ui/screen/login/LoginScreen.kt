package com.dailyapps.feature.auth.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.dailyapps.common.components.BaseButton
import com.dailyapps.common.components.BaseTextField
import com.dailyapps.common.components.PermissionScreen
import com.dailyapps.common.fontSemiBold
import com.dailyapps.common.utils.NavRoute
import com.dailyapps.common.utils.Toast
import com.dailyapps.common.utils.Unauthorized
import com.dailyapps.feature.auth.R
import com.dailyapps.feature.auth.ui.screen.AuthViewModel
import kotlinx.coroutines.delay
import timber.log.Timber
import com.dailyapps.common.R as commonR

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginScreen(
    navHostController: NavHostController,
    viewModel: AuthViewModel
) {


    val notifPermission = rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)

    PermissionScreen(
        permissinStatus = notifPermission.status
    ) { notifPermission.launchPermissionRequest() }

    SideEffect {
        notifPermission.launchPermissionRequest()
    }

    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.loginLoadingState.collectAsState()
    val btnLoginEnableState by viewModel.btnLoginEnableState.collectAsState(initial = false)

    val userLoginResponse by viewModel.loginScreenState.collectAsState()
    val storeUserResponse by viewModel.userStoreResponse.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userLoginResponse) {
        when (userLoginResponse) {
            is LoginScreenState.Success -> {
                (userLoginResponse as LoginScreenState.Success).user.let { user ->
                    with(user) {
                        viewModel.storeUser(this)
                        viewModel.storeId(this.id ?: 0)
                        viewModel.storeToken(this.token ?: "")
//                        viewModel.storeTokenType(this.tokenType ?: "")
                        viewModel.storeUsername(user.email ?: "")
                        Timber.i("Token :: ${this.token ?: ""}")
                    }
                    delay(3000)
                    viewModel.updateLoginLoadingState(false)
                }
            }
            is LoginScreenState.Loading -> viewModel.updateLoginLoadingState(true)
            is LoginScreenState.Error -> {
                viewModel.updateLoginLoadingState(false)
                val errorMessage = (userLoginResponse as LoginScreenState.Error).message
                val message = if (errorMessage.Unauthorized) "Password atau username salah" else errorMessage
                Toast.show(context, message)
            }
            else -> {
            }
        }
    }

    LaunchedEffect(storeUserResponse) {
        if (storeUserResponse) {
            navHostController.navigate(NavRoute.homeScreen)
        }
    }

    Scaffold(modifier = Modifier.fillMaxWidth()) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = commonR.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(125.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    text = "SMP Santo Yosef",
                    fontFamily = fontSemiBold,
                    fontSize = 16.sp,
                )

                BaseTextField(
                    modifier = Modifier.padding(contentPadding),
                    value = username,
                    title = stringResource(R.string.nisn),
                    keyboardType = KeyboardType.Text,
                    onValueChange = viewModel::updateUsername,
                    placeholder = stringResource(R.string.hint_nisn),
                    enable = !isLoading
                )
                BaseTextField(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(top = 16.dp),
                    value = password,
                    title = stringResource(R.string.password),
                    keyboardType = KeyboardType.Password,
                    onValueChange = viewModel::updatePassword,
                    placeholder = stringResource(R.string.hint_password),
                    enable = !isLoading
                )
                BaseButton(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(top = 42.dp)
                        .height(56.dp)
                        .fillMaxWidth(),
                    text = stringResource(R.string.login),
                    isLoading = isLoading,
                    enabled = btnLoginEnableState
                ) {
                    viewModel.updateLoginLoadingState(true)
                    if (username.isEmpty() || password.isEmpty()) {
                        viewModel.updateLoginLoadingState(false)
                        Toast.show(context, "Username dan Password tidak boleh kosong")
                    } else viewModel.login(username, password)

                }
            }
            /*Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 72.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.address),
                fontFamily = fontRegular,
                fontSize = 12.sp,
            )*/
        }
    }
}
