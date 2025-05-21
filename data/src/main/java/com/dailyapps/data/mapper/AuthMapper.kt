package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.LoginApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.User
import javax.inject.Inject

class AuthMapper @Inject constructor(): Mapper<BaseResponse<LoginApiResponse>, User> {
    override fun mapFromApiResponse(type: BaseResponse<LoginApiResponse>): User {
        val data = type.data
        return User(
            id = data.id ?: 0,
            name = data.name ?: "",
            email = data.email ?: "",
            nim = data.nim ?: "",
            address = data.address ?: "",
            role = data.role ?: "",
            stase = data.stase ?: "",
            startSchoolYear = data.startSchoolYear ?: 0,
            endSchoolYear = data.endSchoolYear ?: 0,
            token = data.token ?: ""
        )
    }

}