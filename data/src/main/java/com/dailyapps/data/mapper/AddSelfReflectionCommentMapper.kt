package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.SelfReflectionCommentApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.SelfReflectionComment
import javax.inject.Inject

class AddSelfReflectionCommentMapper @Inject constructor() : Mapper<BaseResponse<SelfReflectionCommentApiResponse>, SelfReflectionComment> {
    override fun mapFromApiResponse(type: BaseResponse<SelfReflectionCommentApiResponse>): SelfReflectionComment {
        val apiComment = type.data

        return SelfReflectionComment(
            id = apiComment.id,
            userId = apiComment.userId,
            rating = apiComment.rating,
            comment = apiComment.comment,
        )
    }
}
