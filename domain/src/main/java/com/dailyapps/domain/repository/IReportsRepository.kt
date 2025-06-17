package com.dailyapps.domain.repository

import com.dailyapps.domain.usecase.GetReportsByUserIdByDateUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Report
import kotlinx.coroutines.flow.Flow

interface IReportsRepository {
    suspend fun getReportsByUserIdByDate(params: GetReportsByUserIdByDateUseCase.Params): Flow<Resource<List<Report>>>
    suspend fun downloadReport(id: Long, token: String): Flow<Resource<String>>
}