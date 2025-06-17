package com.dailyapps.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyapps.domain.usecase.ChangePasswordUseCase
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Student
import com.dailyapps.entity.User
import com.dailyapps.home.changepassword.ChangePasswordScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val masterUseCase: MasterUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
    ) : ViewModel(){

    //private val _user = MutableStateFlow(User.EMPTY)
    private val _student = MutableStateFlow(Student.EMPTY)

    //internal val user get() = _user
    internal val student get() = _student

    private val _changePassword = MutableStateFlow<ChangePasswordScreenState>(ChangePasswordScreenState.Empty)
    val changePassword: StateFlow<ChangePasswordScreenState> get() = _changePassword

    val isPasswordValid: MutableState<Boolean> = mutableStateOf(false)
    val isConfirmPasswordValid: MutableState<Boolean> = mutableStateOf(false)
    val passwordMsg: MutableState<String> = mutableStateOf("")
    val confirmMsg: MutableState<String> = mutableStateOf("")

    private val _currentToken = MutableStateFlow("")
    private val currentToken get() = _currentToken

     val user: StateFlow<User?> = userUseCase.getUser()
            .flowOn(Dispatchers.IO)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    init {
        getLocal()
    }

    private fun getLocal() {
        viewModelScope.launch {
            userUseCase.getCurrentToken().onEach { token ->
                _currentToken.value = token
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            userUseCase.getUser().collect {
                //_user.value = it
            }
        }
    }

    internal fun logout() {
        viewModelScope.launch {
        userUseCase.logout()
        }
    }

    internal fun changePassword(token: String, oldPass: String, newPass: String, newPassAgain: String) {
        viewModelScope.launch {
            changePasswordUseCase.execute(ChangePasswordUseCase.Params(token, oldPass = oldPass, newPass = newPass, newPassAgain = newPassAgain)).collect { result ->
                when(result) {
                    is Resource.Loading -> _changePassword.value = ChangePasswordScreenState.Loading
                    is Resource.Success -> _changePassword.value = ChangePasswordScreenState.Success(result.data)
                    is Resource.Error -> _changePassword.value = ChangePasswordScreenState.Error(result.msg)
                }
            }
        }
    }

    fun validateConfirmPassword(inputPassword: String, newPassword: String) {
        if (inputPassword != newPassword) {
            isConfirmPasswordValid.value = true
            confirmMsg.value = "Password tidak cocok"
        }
        else {
            isConfirmPasswordValid.value = false
            confirmMsg.value = ""
        }
    }

    fun validatePassword(inputPassword: String) {
        if (inputPassword.length < 8) {
            isPasswordValid.value = true
            passwordMsg.value = "Password minimal 8 karakter"
        }
        else {
            isPasswordValid.value = false
            passwordMsg.value = ""
        }
    }

}