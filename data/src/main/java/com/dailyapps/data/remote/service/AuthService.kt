package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ChangePasswordResponse
import com.dailyapps.apiresponse.LoginApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") userName: String,
        @Field("password") password: String
    ): BaseResponse<LoginApiResponse>


    @FormUrlEncoded
    @POST("user/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("password_lama") oldPassword: String,
        @Field("password_baru") newPassword: String,
        @Field("konfirmasi_password_baru") newPasswordAgain: String,
    ): BaseResponse<ChangePasswordResponse>
}