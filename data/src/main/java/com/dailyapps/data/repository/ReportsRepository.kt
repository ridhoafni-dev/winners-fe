package com.dailyapps.data.repository

import com.dailyapps.data.remote.datasource.ReportsRemoteDataSource
import com.dailyapps.domain.repository.IReportsRepository
import com.dailyapps.domain.usecase.GetReportsByUserIdByDateUseCase
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
}