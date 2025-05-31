package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.AddObservationResponse
import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ObservationApiResponse
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

    @Multipart
    @POST("observations")
    suspend fun addObservation(
        @Header("Authorization") token: String,
        @Part("userId") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("date") date: RequestBody,
        @Part("lecturerId") lecturerId: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseResponse<AddObservationResponse>

    @Multipart
    @PATCH("observations/{id}")
    suspend fun updateObservation(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Part("userId") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("date") date: RequestBody,
        @Part("active") active: RequestBody,
        @Part("lecturerId") lecturerId: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseResponse<AddObservationResponse>
}