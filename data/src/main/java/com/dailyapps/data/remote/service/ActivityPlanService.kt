package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.ActivityPlanApiResponse
import com.dailyapps.apiresponse.ActivityPlanCommentResponse
import com.dailyapps.apiresponse.AddActivityPlanResponse
import com.dailyapps.apiresponse.AddObservationResponse
import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ObservationApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivityPlanService {
    @GET("plans")
    suspend fun getActivityPlansByUserIdByDate(
        @Header("Authorization") token: String,
        @Query("id") userId: Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("lecturer") lecturer: Boolean,
    ) : BaseResponse<List<ActivityPlanApiResponse>>

    @GET("plans/{id}")
    suspend fun getActivityPlanById(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
    ) : BaseResponse<ActivityPlanApiResponse>

    @FormUrlEncoded
    @POST("plans")
    suspend fun addActivityPlan(
        @Header("Authorization") token: String,
        @Field("userId") userId: Long,
        @Field("name") name: String,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String,
        @Field("active") active: Boolean,
        @Field("status") status: String,
        @Field("lecturerId") lecturerId: Long,
    ): BaseResponse<AddActivityPlanResponse>

    @FormUrlEncoded
    @PATCH("plans/{id}")
    suspend fun updateActivityPlan(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Field("userId") userId: Long,
        @Field("name") name: String,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String,
        @Field("active") active: Boolean,
        @Field("status") status: String,
        @Field("lecturerId") lecturerId: Long,
    ): BaseResponse<AddActivityPlanResponse>

    @FormUrlEncoded
    @POST("plans/comment/{id}")
    suspend fun addActivityPlanComment(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Field("rating") rating: Int,
        @Field("comment") comment: String
    ): BaseResponse<ActivityPlanCommentResponse>
}

