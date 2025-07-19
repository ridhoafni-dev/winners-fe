package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.SelfReflectionApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.SelfReflection
import com.dailyapps.entity.SelfReflectionLecturer
import com.dailyapps.entity.SelfReflectionComment
import com.dailyapps.entity.User
import javax.inject.Inject

class SelfReflectionDetailMapper @Inject constructor(): Mapper<BaseResponse<SelfReflectionApiResponse>, SelfReflection> {
    override fun mapFromApiResponse(type: BaseResponse<SelfReflectionApiResponse>): SelfReflection {
        val response = type.data
        return SelfReflection(
            id = response.id,
            title = response.title,
            active = response.active,
            userId = response.userId,
            createAt = response.createAt,
            updatedAt = response.updatedAt,
            user = mapUser(response.user),
            selfReflectionLecturer = mapSelfReflectionLecturer(response.selfReflectionLecturer),
            selfReflectionComment = mapSelfReflectionComment(response.selfReflectionCommentResponse)
        )
    }

    private fun mapUser(apiUser: com.dailyapps.apiresponse.UserApiResponse?): User? {
        if (apiUser == null) return null

        return User(
            id = apiUser.id,
            email = apiUser.email,
            role = apiUser.role,
            name = apiUser.profile?.name
        )
    }

    private fun mapSelfReflectionLecturer(apiLecturer: com.dailyapps.apiresponse.SelfReflectionLecturerApiResponse?): SelfReflectionLecturer? {
        if (apiLecturer == null) return null

        return SelfReflectionLecturer(
            userId = apiLecturer.userId,
            name = apiLecturer.name,
        )
    }

    private fun mapSelfReflectionComment(apiComment: com.dailyapps.apiresponse.SelfReflectionCommentApiResponse?): SelfReflectionComment? {
        if (apiComment == null) return null

        return SelfReflectionComment(
            id = apiComment.id,
            comment = apiComment.comment,
            userId = apiComment.userId,
            rating = apiComment.rating
        )
    }
}
