package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ObservationApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ObservationService {
    @GET("observations")
    suspend fun getObservationsByUserIdByDate(
        @Header("Authorization") token: String,
        @Query("id") userId: Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("lecturer") lecturer: Boolean,
    ) : BaseResponse<List<ObservationApiResponse>>

    @GET("observations/{id}")
    suspend fun getObservationById(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
    ) : BaseResponse<ObservationApiResponse>
}