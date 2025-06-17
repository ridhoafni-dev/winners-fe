package com.dailyapps.data.remote.service

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ClassRoomApiResponse
import com.dailyapps.apiresponse.SchoolYearApiResponse
import com.dailyapps.apiresponse.TeachersApiResponse
import retrofit2.http.GET

interface MasterService {
    @GET("tahun-ajaran")
    suspend fun getAllStudentYear(): BaseResponse<List<SchoolYearApiResponse>>

    @GET("auth/lecturers")
    suspend fun getAllTeacher(): BaseResponse<List<TeachersApiResponse>>

    @GET("kelas")
    suspend fun getAllClassRoom(): BaseResponse<List<ClassRoomApiResponse>>
}