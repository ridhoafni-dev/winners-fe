
package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IReportsRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Report
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostReportUseCase @Inject constructor(
    private val repository: IReportsRepository
) : ApiUseCaseParams<PostReportUseCase.Params, Report> {

    override suspend fun execute(params: Params): Flow<Resource<Report>> =
        repository.postReport(params)

    data class Params(
        val userId: Long,
        val date: String,
        val lecturerId: Long,
        val documentUri: String,
        val token: String
    )
}