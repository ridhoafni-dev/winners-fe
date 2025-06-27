package com.dailyapps.data.repository

import com.dailyapps.data.remote.datasource.ReportsRemoteDataSource
import com.dailyapps.domain.repository.IReportsRepository
import com.dailyapps.domain.usecase.GetReportByIdUseCase
import com.dailyapps.domain.usecase.GetReportsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.PostReportUseCase
import com.dailyapps.domain.usecase.UpdateReportUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Report
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportsRepository @Inject constructor(
    private val remoteDataSource: ReportsRemoteDataSource
) : IReportsRepository {

    override suspend fun getReportsByUserIdByDate(params: GetReportsByUserIdByDateUseCase.Params): Flow<Resource<List<Report>>> =
        remoteDataSource.getReportsByUserIdByDate(params)

    override suspend fun downloadReport(id: Long, token: String): Flow<Resource<String>> =
        remoteDataSource.downloadReport(id, token)

    override suspend fun postReport(params: PostReportUseCase.Params): Flow<Resource<Report>> =
        remoteDataSource.postReport(params)

    override suspend fun getReportById(params: GetReportByIdUseCase.Params): Flow<Resource<Report>> =
        remoteDataSource.getReportById(params)

    override suspend fun updateReport(params: UpdateReportUseCase.Params): Flow<Resource<Report>> =
        remoteDataSource.updateReport(params)
}