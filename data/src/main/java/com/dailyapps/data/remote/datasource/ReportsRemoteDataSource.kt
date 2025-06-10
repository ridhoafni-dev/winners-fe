package com.dailyapps.data.remote.datasource

import com.dailyapps.common.utils.formatToken
import com.dailyapps.data.mapper.DownloadReportMapper
import com.dailyapps.data.mapper.ReportMapper
import com.dailyapps.data.remote.service.ReportsService
import com.dailyapps.data.utils.apiCall
import com.dailyapps.data.utils.mapFromApiResponse
import com.dailyapps.domain.usecase.GetReportsByUserIdByDateUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Report
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportsRemoteDataSource @Inject constructor(
    private val reportsService: ReportsService,
    private val reportMapper: ReportMapper,
    private val downloadReportMapper: DownloadReportMapper
) {
    suspend fun getReportsByUserIdByDate(params: GetReportsByUserIdByDateUseCase.Params): Flow<Resource<List<Report>>> {
        return mapFromApiResponse(
            result = apiCall {
                reportsService.getReportsByUserIdByDate(
                    params.token.formatToken(),
                    params.userId,
                    params.startDate,
                    params.endDate,
                    params.lecturer
                )
            },
            reportMapper
        )
    }

    suspend fun downloadReport(id: Long, token: String): Flow<Resource<String>> {
        return mapFromApiResponse(
            result = apiCall {
                reportsService.downloadReport(token.formatToken(), id)
            },
            downloadReportMapper
        )
    }
}
