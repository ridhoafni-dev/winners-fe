package com.dailyapps.home.changepassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dailyapps.common.components.BaseAppBar
import com.dailyapps.common.components.BaseButton
import com.dailyapps.common.components.BaseTextField
import com.dailyapps.common.utils.Toast
import com.dailyapps.common.utils.Unauthorized
import com.dailyapps.home.HomeViewModel
import timber.log.Timber

@Composable
fun ChangePasswordScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel
) {

    var isLoading by rememberSaveable { mutableStateOf(false) }

    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var newPasswordAgain by rememberSaveable { mutableStateOf("") }

    val changePassword by viewModel.changePassword.collectAsState()
    val context = LocalContext.current

    //val currentTokenTypeStudentId by viewModel.currentTokenTypeStudentId.collectAsState()
    val currentTokenTypeStudentId = Pair(0, 0)

    val (token, tokenType) = currentTokenTypeStudentId

    LaunchedEffect(changePassword) {
        when (changePassword) {
            is ChangePasswordScreenState.Success -> {
                isLoading = false
                Toast.show(context, "Password berhasil diubah")
                viewModel.logout()
            }
            is ChangePasswordScreenState.Loading -> isLoading = true
            is ChangePasswordScreenState.Error -> {
                isLoading = false
                val errorMessage = (changePassword as ChangePasswordScreenState.Error).message
                val message = if (errorMessage.Unauthorized) "Password atau username salah" else errorMessage
                Toast.show(context, message)
            }
            else -> {
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BaseAppBar(
                title = "Ganti Password",
                modifier = Modifier.fillMaxWidth(),
                onClickBack = { navHostController.popBackStack() },
                onMenuClick = {})
        },
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Timber.e("$contentPadding")
                Spacer(modifier = Modifier.height(24.dp))
                BaseTextField(
                    modifier = Modifier
                        .padding(contentPadding),
                    value = oldPassword,
                    title = "Password Lama",
                    keyboardType = KeyboardType.Password,
                    onValueChange = {
                        oldPassword = it
                    },
                    placeholder = "Masukan Password Lama",
                    enable = !isLoading,
                )
                BaseTextField(
                    modifier = Modifier
                        .padding(top =16.dp),
                    value = newPassword,
                    title = "Password Baru",
                    keyboardType = KeyboardType.Password,
                    onValueChange = {
                        newPassword = it
                        viewModel.validatePassword(it)
                    },
                    placeholder = "Masukan Password Baru",
                    enable = !isLoading,
                    isError = viewModel.isPasswordValid.value,
                    errorMessage = viewModel.passwordMsg.value
                )

                BaseTextField(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    value = newPasswordAgain,
                    title = "Ulangi Password Baru",
                    keyboardType = KeyboardType.Password,
                    onValueChange = {
                        newPasswordAgain = it
                        viewModel.validateConfirmPassword(it, newPassword)
                      },
                    placeholder = "Masukan Ulang Password Baru",
                    enable = !isLoading,
                    isError = viewModel.isConfirmPasswordValid.value,
                    errorMessage = viewModel.confirmMsg.value
                )
                BaseButton(
                    modifier = Modifier
                        .padding(top = 42.dp)
                        .height(56.dp)
                        .fillMaxWidth(),
                    text = "Simpan",
                    isLoading = isLoading,
                    enabled = isLoading || (oldPassword.isNotEmpty() && newPassword.isNotEmpty() && newPasswordAgain.isNotEmpty()) && !viewModel.isConfirmPasswordValid.value
                ) {
                    isLoading = true
                    if (oldPassword.isEmpty()) {
                        isLoading = false
                        Toast.show(context, "Password Lama tidak boleh kosong")
                    } else if(newPassword.isEmpty()) {
                        isLoading = false
                        Toast.show(context, "Password Baru tidak boleh kosong")
                    } else if(newPasswordAgain.isEmpty()) {
                        isLoading = false
                        Toast.show(context, "Masukkan lagi Password Baru")
                    } else {
                        viewModel.changePassword("$tokenType $token", oldPassword, newPassword, newPasswordAgain)
                    }

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
