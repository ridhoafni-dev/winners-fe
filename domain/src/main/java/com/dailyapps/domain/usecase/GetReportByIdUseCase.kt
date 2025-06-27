package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IReportsRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Report
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReportByIdUseCase @Inject constructor(
    private val repository: IReportsRepository
) : ApiUseCaseParams<GetReportByIdUseCase.Params, Report> {

    override suspend fun execute(params: Params): Flow<Resource<Report>> =
        repository.getReportById(params)

    data class Params(
        val id: Long,
        val token: String
    )
}
