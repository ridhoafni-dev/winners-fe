package com.dailyapps.domain.repository

import com.dailyapps.domain.usecase.GetReportByIdUseCase
import com.dailyapps.domain.usecase.GetReportsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.PostReportUseCase
import com.dailyapps.domain.usecase.UpdateReportUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Report
import kotlinx.coroutines.flow.Flow

interface IReportsRepository {
    suspend fun getReportsByUserIdByDate(params: GetReportsByUserIdByDateUseCase.Params): Flow<Resource<List<Report>>>
    suspend fun downloadReport(id: Long, token: String): Flow<Resource<String>>
    suspend fun postReport(params: PostReportUseCase.Params): Flow<Resource<Report>>
    suspend fun getReportById(params: GetReportByIdUseCase.Params): Flow<Resource<Report>>
    suspend fun updateReport(params: UpdateReportUseCase.Params): Flow<Resource<Report>>
}
