package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.SelfReflectionApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.SelfReflectionComment
import com.dailyapps.entity.User
import javax.inject.Inject

class AddSelfReflectionCommentMapper @Inject constructor() : Mapper<BaseResponse<SelfReflectionApiResponse>, SelfReflectionComment> {
    override fun mapFromApiResponse(type: BaseResponse<SelfReflectionApiResponse>): SelfReflectionComment {
        val apiComment = type.data.selfReflectionCommentResponse ?: throw IllegalArgumentException("Comment response is null")

        return SelfReflectionComment(
            id = apiComment.id,
            userId = apiComment.userId,
            rating = apiComment.rating,
            comment = apiComment.comment,
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
}
