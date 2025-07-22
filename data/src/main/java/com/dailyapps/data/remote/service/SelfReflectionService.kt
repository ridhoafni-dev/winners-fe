package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.SelfReflectionApiResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SelfReflectionService {
    @GET("reflections/{userId}/{startDate}/{endDate}/{lecturer}")
    suspend fun getSelfReflectionsByUserIdByDate(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Path("lecturer") lecturer: Boolean
    ): BaseResponse<List<SelfReflectionApiResponse>>

    @GET("reflections/{id}")
    suspend fun getSelfReflectionById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): BaseResponse<SelfReflectionApiResponse>

    @POST("reflections")
    suspend fun addSelfReflection(
        @Header("Authorization") token: String,
        @Body request: Map<String, Any>
    ): BaseResponse<SelfReflectionApiResponse>

    @PUT("reflections/{id}")
    suspend fun updateSelfReflection(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body request: Map<String, Any>
    ): BaseResponse<SelfReflectionApiResponse>

    @POST("reflections/comments/{id}")
    suspend fun addSelfReflectionComment(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Field("userId") userId: Long,
        @Field("rating") rating: Int,
        @Field("comment") comment: String
    ): BaseResponse<SelfReflectionApiResponse>
}
