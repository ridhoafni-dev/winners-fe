package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IReportsRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Report
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReportsByUserIdByDateUseCase @Inject constructor(
    private val repository: IReportsRepository
) : ApiUseCaseParams<GetReportsByUserIdByDateUseCase.Params, List<Report>> {
    override suspend fun execute(params: Params): Flow<Resource<List<Report>>> =
        repository.getReportsByUserIdByDate(params)

    data class Params(
        val userId: Long,
        val startDate: String,
        val endDate: String,
        val lecturer: Boolean,
        val token: String
    )
}