package com.dailyapps.feature.report

import androidx.lifecycle.viewModelScope
import com.dailyapps.common.utils.ViewModelState
import com.dailyapps.domain.usecase.DownloadReportUseCase
import com.dailyapps.domain.usecase.GetReportByIdUseCase
import com.dailyapps.domain.usecase.GetReportsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.PostReportUseCase
import com.dailyapps.domain.usecase.UpdateReportUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.report.state.ReportAction
import com.dailyapps.feature.report.state.ReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getReportsByUserIdByDate: GetReportsByUserIdByDateUseCase,
    private val downloadReportUseCase: DownloadReportUseCase,
    private val postReportUseCase: PostReportUseCase,
    private val getReportByIdUseCase: GetReportByIdUseCase,
    private val updateReportUseCase: UpdateReportUseCase,
    private val userUseCase: UserUseCase,
    private val masterUseCase: MasterUseCase
) : ViewModelState<ReportState, ReportAction>(
    initialState = ReportState()
) {

    init {
        getLocal()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getLocal() {
        viewModelScope.launch {
            userUseCase.getUser()
                .distinctUntilChanged()
                .onEach { user ->
                    update {
                        copy(
                            userId = user.id?.toLong() ?: 0L,
                            token = user.token ?: "",
                            role = user.role ?: ""
                        )
                    }
                }
                .flatMapLatest { user ->
                    val token = user.token ?: ""
                    if (token.isNotEmpty()) masterUseCase.getAllTeachersAsFlow()
                    else flowOf(emptyList())
                }
                .collectLatest { teachers ->
                    update { copy(form = form.copy(lecturers = teachers)) }
                }
        }
    }

    override fun handleAction(action: ReportAction) {
        when (action) {
            is ReportAction.OnGetReports -> getReports(
                action.userId,
                action.startDate,
                action.endDate,
                action.token
            )

            is ReportAction.OnUpdateDateRange -> {
                update {
                    copy(
                        list = list.copy(
                            startDate = action.startDate,
                            endDate = action.endDate
                        )
                    )
                }
            }

            is ReportAction.OnDownloadReport -> downloadReport(action.id, action.token)

            is ReportAction.OnResetDownloadState -> {
                update {
                    copy(
                        downloading = false,
                        downloadSuccess = false,
                        downloadError = false,
                        downloadErrorMessage = "",
                        downloadUrl = ""
                    )
                }
            }

            is ReportAction.OnResetState -> {
                restartState()
            }

            is ReportAction.OnReportValueChange -> {
                update {
                    copy(
                        form = form.copy(
                            date = action.date,
                            lecturerId = action.lecturerId,
                            documentUri = action.documentUri ?: ""
                        )
                    )
                }
            }

            is ReportAction.OnSubmitReport -> onSubmitReport()

            is ReportAction.OnGetReport -> getReportById(action.id, action.token)

            is ReportAction.OnUpdateReport -> onUpdateReport(action.id)
        }
    }

    private fun getReports(
        userId: Long,
        startDate: String,
        endDate: String,
        token: String
    ) {
        viewModelScope.launch {
            getReportsByUserIdByDate.execute(
                GetReportsByUserIdByDateUseCase.Params(
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
                                        reports = resource.data
                                    ),
                                    isSuccess = true,
                                    isEmpty = resource.data.isEmpty()
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

    private fun onSubmitReport() {
        viewModelScope.launch {
            postReportUseCase.execute(
                PostReportUseCase.Params(
                    currentState().userId,
                    currentState().form.date,
                    currentState().form.lecturerId,
                    currentState().form.documentUri,
                    currentState().token
                )
            )
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    isLoading = false,
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

    private fun getReportById(id: Long, token: String) {
        viewModelScope.launch {
            getReportByIdUseCase.execute(GetReportByIdUseCase.Params(id, token))
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    isLoading = false,
                                    detail = detail.copy(
                                        report = resource.data
                                    )
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

    private fun onUpdateReport(id: Long) {
        viewModelScope.launch {
            updateReportUseCase.execute(
                UpdateReportUseCase.Params(
                    id,
                    currentState().userId,
                    currentState().form.date,
                    currentState().form.lecturerId,
                    currentState().form.documentUri,
                    currentState().token
                )
            )
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    isLoading = false,
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

    private fun downloadReport(id: Long, token: String) {
        viewModelScope.launch {
            downloadReportUseCase.execute(DownloadReportUseCase.Params(id, token))
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    downloading = false,
                                    downloadSuccess = true,
                                    downloadUrl = resource.data
                                )
                            }
                        }

                        is Resource.Error -> {
                            update {
                                copy(
                                    downloading = false,
                                    downloadError = true,
                                    downloadErrorMessage = resource.msg
                                )
                            }
                        }

                        Resource.Loading -> {
                            update {
                                copy(
                                    downloading = true,
                                    downloadError = false,
                                    downloadSuccess = false,
                                    downloadErrorMessage = ""
                                )
                            }
                        }
                    }
                }
        }
    }
}