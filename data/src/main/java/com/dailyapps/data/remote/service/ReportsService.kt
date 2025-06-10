package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ReportApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportsService {
    @GET("reports")
    suspend fun getReportsByUserIdByDate(
        @Header("Authorization") token: String,
        @Query("id") userId: Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("lecturer") lecturer: Boolean,
    ): BaseResponse<List<ReportApiResponse>>

    @GET("reports/download/{id}")
    suspend fun downloadReport(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): BaseResponse<String> // Assuming the download URL is returned as a string
}