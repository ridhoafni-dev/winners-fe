package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IReportsRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DownloadReportUseCase @Inject constructor(
    private val repository: IReportsRepository
) : ApiUseCaseParams<DownloadReportUseCase.Params, String> {
    override suspend fun execute(params: Params): Flow<Resource<String>> =
        repository.downloadReport(params.id, params.token)

    data class Params(val id: Long, val token: String)
}