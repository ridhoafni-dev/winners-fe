package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.MemoCommentResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.MemoComment
import javax.inject.Inject

class AddMemoCommentMapper @Inject constructor() : Mapper<BaseResponse<MemoCommentResponse>, MemoComment> {
    override fun mapFromApiResponse(type: BaseResponse<MemoCommentResponse>): MemoComment {
        val data = type.data
        return MemoComment(
            id = data.id,
            memoId = data.memoId,
            userId = data.userId,
            rating = data.rating,
            comment = data.comment,
        )
    }

}