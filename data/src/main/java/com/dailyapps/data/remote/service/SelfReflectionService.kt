package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.SelfReflectionApiResponse
import com.dailyapps.apiresponse.SelfReflectionCommentApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SelfReflectionService {
    @GET("reflections/{userId}/{startDate}/{endDate}/{lecturer}")
    suspend fun getSelfReflectionsByUserIdByDate(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Path("lecturer") lecturer: Int
    ): BaseResponse<List<SelfReflectionApiResponse>>

    @GET("reflections/{id}")
    suspend fun getSelfReflectionById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): BaseResponse<SelfReflectionApiResponse>

    @FormUrlEncoded
    @POST("reflections")
    suspend fun addSelfReflection(
        @Header("Authorization") token: String,
        @Field("userId") userId: Long,
        @Field("description") description: String,
        @Field("lecturerId") lecturerId: Long
    ): BaseResponse<SelfReflectionApiResponse>

    @FormUrlEncoded
    @PATCH("reflections/{id}")
    suspend fun updateSelfReflection(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Field("userId") userId: Long,
        @Field("description") description: String,
        @Field("lecturerId") lecturerId: Long
    ): BaseResponse<SelfReflectionApiResponse>

    @FormUrlEncoded
    @POST("reflections/comment/{id}")
    suspend fun addSelfReflectionComment(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Field("userId") userId: Long,
        @Field("rating") rating: Int,
        @Field("comment") comment: String
    ): BaseResponse<SelfReflectionCommentApiResponse>
}
