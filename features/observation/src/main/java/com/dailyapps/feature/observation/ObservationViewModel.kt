package com.dailyapps.feature.observation

import androidx.lifecycle.viewModelScope
import com.dailyapps.domain.usecase.GetObservationByIdUseCase
import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.feature.observation.state.ObservationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObservationViewModel @Inject constructor(
    private val getObservationsByUserIdByDate: GetObservationsByUserIdByDateUseCase,
    private val getObservationByUseCase: GetObservationByIdUseCase,
    private val userUseCase: UserUseCase
) : ViewModelState<ObservationState, ObservationAction>(
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
                        list = list.copy(
                            userId = user.id?.toLong() ?: 0L
                        ),
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

            is ObservationAction.OnGetObservation -> {
                getObservation(
                    action.id,
                    action.token
                )
            }

            is ObservationAction.OnUpdateDateRange -> {
                update {
                    copy(
                        list = list.copy(
                            startDate = action.startDate,
                            endDate = action.endDate
                        )
                    )
                }
            }

            is ObservationAction.OnAddObservation -> {
                update {
                    copy(
                        add = add.copy(
                            name = action.name,
                            description = action.description,
                            date = action.date,
                            lecturer = action.lecturerId,
                            image = action.imageUri ?: ""
                        )
                    )
                }
            }
        }
    }

    fun resetState() {
        // Reset to initial state
        update { ObservationState() }
        // Cancel any ongoing jobs if needed
        viewModelScope.coroutineContext.cancelChildren()
    }

    private fun getObservations(
        userId: Long,
        startDate: String,
        endDate: String,
        token: String
    ) {
        viewModelScope.launch {
            getObservationsByUserIdByDate.execute(
                GetObservationsByUserIdByDateUseCase.Params(
                    userId,
                    startDate,
                    endDate,
                    false,
                    token
                )
            )
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    isLoading = false,
                                    list = list.copy(
                                        observations = resource.data
                                    ),
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

    private fun getObservation(
        id: Long,
        token: String
    ) {
        viewModelScope.launch {
            getObservationByUseCase.execute(
                GetObservationByIdUseCase.Params(id, token)
            )
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    isLoading = false,
                                    detail = detail.copy(
                                        observation = resource.data
                                    ),
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