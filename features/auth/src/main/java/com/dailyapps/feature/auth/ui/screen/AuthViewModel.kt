package com.dailyapps.feature.auth.ui.screen

import androidx.lifecycle.viewModelScope
import com.dailyapps.domain.usecase.AuthUseCase
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.StudentUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Student
import com.dailyapps.entity.User
import com.dailyapps.entity.isAdmin
import com.dailyapps.feature.auth.BaseViewModel
import com.dailyapps.feature.auth.ui.screen.login.LoginScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase,
    private val masterUserCase: MasterUseCase,
    private val studentUseCase: StudentUseCase
) : BaseViewModel() {

    private val _loginScreenState = MutableStateFlow<LoginScreenState>(LoginScreenState.Empty)
    val loginScreenState: StateFlow<LoginScreenState> get() = _loginScreenState

    private val _userStoreResponse = MutableStateFlow(false)
    val userStoreResponse: StateFlow<Boolean> get() = _userStoreResponse

    private val _username = MutableStateFlow("")
    val username get() = _username

    private val _password = MutableStateFlow("")
    val password get() = _password

    private val _loginLoadingState = MutableStateFlow(false)
    val loginLoadingState get() = _loginLoadingState

    val btnLoginEnableState get() = _password.combine(_password) { username, password ->
        username.isNotEmpty() && password.isNotEmpty()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun login(username: String, password: String) {
        viewModelScope.launch {
            loadMasterData()
                .flatMapConcat {
                    when (it) {
                        is Resource.Loading -> flowOf(Resource.Loading)
                        is Resource.Success -> authUseCase.execute(
                            AuthUseCase.Params(
                                username,
                                password
                            )
                        )
                        is Resource.Error -> flowOf(Resource.Error(it.msg))
                    }
                }
//                .flatMapConcat { userResource ->
//                    when (userResource) {
//                        is Resource.Success -> { flowOf(userResource)
//                            val user = userResource.data
//                            if (user.isAdmin) flowOf(userResource)
//                            else loadInitialData("${user.tokenType} ${user.token}", user.id ?: 0)
//                                .flatMapConcat {
//                                    when (it) {
//                                        is Resource.Loading -> flowOf(Resource.Loading)
//                                        is Resource.Success ->  flowOf(userResource)
//                                        is Resource.Error -> flowOf(Resource.Error(it.msg))
//                                    }
//                                }
//                        }
//                        else -> {
//                            flowOf(userResource)
//                        }
//                    }
//                }
                .filterNot { it is Resource.Loading }
                .onStart { emit(Resource.Loading) }
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _loginScreenState.value = LoginScreenState.Loading
                        }

                        is Resource.Success -> {
                            _loginScreenState.value = LoginScreenState.Success(result.data)
                        }

                        is Resource.Error -> {
                            _loginScreenState.value = LoginScreenState.Error(result.msg)
                        }
                    }
                }
        }
    }

    private suspend fun loadInitialData(token: String, userId: Int): Flow<Resource<List<Student>>> =
        studentUseCase.execute(StudentUseCase.Params(token = token, userId = userId))

    private suspend fun loadMasterData(): Flow<Resource<Any>> =
        masterUserCase.getAllTeachers()

    fun storeUser(user: User) {
        viewModelScope.launch {
            userUseCase.storeUser(user).runFlow(_userStoreResponse)
        }
    }

    fun storeUsername(username: String) {
        viewModelScope.launch {
            userUseCase.storeUsername(username)
        }
    }

    fun storeId(id: Int) {
        viewModelScope.launch {
            userUseCase.storeId(id)
        }
    }

    fun storeToken(token: String) {
        viewModelScope.launch {
            userUseCase.storeToken(token)
        }
    }

    fun storeTokenType(tokenType: String) {
        viewModelScope.launch {
            userUseCase.storeTokenType(tokenType)
        }
    }

    fun updateUsername(username: String) {
        _username.value = username
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateLoginLoadingState(loginLoadingState: Boolean) {
        _loginLoadingState.value = loginLoadingState
    }

}