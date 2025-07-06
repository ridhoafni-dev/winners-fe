package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.MemoApiResponse
import com.dailyapps.apiresponse.AddMemoResponse
import com.dailyapps.apiresponse.MemoCommentResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MemoService {
    @GET("memos")
    suspend fun getMemosByUserIdByDate(
        @Header("Authorization") token: String,
        @Query("id") userId: Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("lecturer") lecturer: Boolean,
    ) : BaseResponse<List<MemoApiResponse>>

    @GET("memos/{id}")
    suspend fun getMemoById(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
    ) : BaseResponse<MemoApiResponse>

    @FormUrlEncoded
    @POST("memos")
    suspend fun addMemo(
        @Header("Authorization") token: String,
        @Field("userId") userId: Long,
        @Field("title") title: String,
        @Field("lecturerId") lecturerId: Long,
    ): BaseResponse<AddMemoResponse>

    @FormUrlEncoded
    @PATCH("memos/{id}")
    suspend fun updateMemo(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Field("userId") userId: Long,
        @Field("title") title: String,
        @Field("lecturerId") lecturerId: Long,
    ): BaseResponse<MemoApiResponse>

    @FormUrlEncoded
    @POST("memos/comment/{id}")
    suspend fun addMemoComment(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Field("userId") userId: Long,
        @Field("rating") rating: Int,
        @Field("comment") comment: String
    ): BaseResponse<MemoCommentResponse>
}
