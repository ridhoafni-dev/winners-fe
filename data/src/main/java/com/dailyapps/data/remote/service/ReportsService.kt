package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ReportApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportsService {
    @GET("reports/{id}/{startDate}/{endDate}/{lecturer}")
    suspend fun getReportsByUserIdByDate(
        @Header("Authorization") token: String,
        @Path("id") userId: Long,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Path("lecturer") lecturer: Boolean,
    ): BaseResponse<List<ReportApiResponse>>

    @GET("reports/download/{id}")
    suspend fun downloadReport(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): BaseResponse<String> // Assuming the download URL is returned as a string

    @Multipart
    @POST("reports")
    suspend fun submitReport(
        @Header("Authorization") token: String,
        @Part("userId") userId: RequestBody,
        @Part("lecturerId") lecturerId: RequestBody,
        @Part("date") date: RequestBody,
        @Part document: MultipartBody.Part
    ): BaseResponse<ReportApiResponse>

    @Multipart
    @PATCH("reports/{id}")
    suspend fun updateReport(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Part("userId") userId: RequestBody,
        @Part("lecturerId") lecturerId: RequestBody,
        @Part("date") date: RequestBody,
        @Part document: MultipartBody.Part
    ): BaseResponse<ReportApiResponse>

    @GET("reports/{id}")
    suspend fun getReportById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): BaseResponse<ReportApiResponse>
}
