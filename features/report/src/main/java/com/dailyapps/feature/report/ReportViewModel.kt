package com.dailyapps.feature.report

import androidx.lifecycle.viewModelScope
import com.dailyapps.common.utils.ViewModelState
import com.dailyapps.domain.usecase.DownloadReportUseCase
import com.dailyapps.domain.usecase.GetReportsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.report.state.ReportAction
import com.dailyapps.feature.report.state.ReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getReportsByUserIdByDate: GetReportsByUserIdByDateUseCase,
    private val downloadReportUseCase: DownloadReportUseCase,
    private val userUseCase: UserUseCase
) : ViewModelState<ReportState, ReportAction>(
    initialState = ReportState()
) {

    init {
        getLocal()
    }

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
                .collectLatest {}
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