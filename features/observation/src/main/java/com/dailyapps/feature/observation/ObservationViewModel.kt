package com.dailyapps.feature.observation

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.feature.observation.state.ObservationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObservationViewModel @Inject constructor(
    private val getObservationsByUserIdByDate: GetObservationsByUserIdByDateUseCase,
    private val userUseCase: UserUseCase
): ViewModelState<ObservationState, ObservationAction>(
    initialState = ObservationState()
) {

    init {
        getLocal()
    }

    private fun getLocal() {
        viewModelScope.launch {
            userUseCase.getUser().collectLatest { user ->
                update {
                    copy(
                        userId = user.id?.toLong() ?: 0L,
                        token = user.token ?: "",
                    )
                }
            }
        }
    }

    override fun handleAction(action: ObservationAction) {
        when (action) {
            is ObservationAction.OnGetObservations -> {
                getObservations(
                    action.userId,
                    action.startDate,
                    action.endDate,
                    action.token
                )
            }
        }
    }

    private fun getObservations(
        userId: Long,
        startDate: String,
        endDate: String,
        token: String
    ) {
        viewModelScope.launch {
            getObservationsByUserIdByDate.execute(
                GetObservationsByUserIdByDateUseCase.Params(userId, startDate, endDate, false, token)
            )
            .collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        update {
                            copy(
                                isLoading = false,
                                observations = resource.data,
                                isSuccess = true
                            )
                        }
                    }
                    is Resource.Error -> {
                        update {
                            copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = resource.msg
                            )
                        }
                    }

                    Resource.Loading -> {
                        update {
                            copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = ""
                            )
                        }
                    }
                }
            }
        }
    }
}