package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.MemoApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.Memo
import com.dailyapps.entity.MemoComment
import com.dailyapps.entity.MemoLecturer
import com.dailyapps.entity.User
import javax.inject.Inject

class MemoMapper @Inject constructor(): Mapper<BaseResponse<List<MemoApiResponse>>, List<Memo>> {
    override fun mapFromApiResponse(type: BaseResponse<List<MemoApiResponse>>): List<Memo> {
        return type.data.map { response ->
            Memo(
                id = response.id,
                title = response.title,
                active = response.active,
                userId = response.userId,
                endDate = response.endDate,
                startDate = response.startDate,
                createAt = response.createAt,
                status = response.status,
                updatedAt = response.updatedAt,
                user = mapUser(response.user),
                memoLecturer = mapMemoLecturer(response.memoLecturer),
                memoComment = mapMemoComment(response.memoCommentResponse)
            )
        }
    }

    private fun mapUser(apiUser: com.dailyapps.apiresponse.User?): User? {
        if (apiUser == null) return null

        return User(
            id = apiUser.id,
            email = apiUser.email,
            role = apiUser.role
        )
    }

    private fun mapMemoLecturer(apiLecturer: com.dailyapps.apiresponse.MemoLecturer?): MemoLecturer? {
        if (apiLecturer == null) return null

        return MemoLecturer(
            userId = apiLecturer.userId
        )
    }

    private fun mapMemoComment(apiComment: com.dailyapps.apiresponse.MemoCommentResponse?): MemoComment? {
        if (apiComment == null) return null

        return MemoComment(
            id = apiComment.id,
            memoId = apiComment.memoId,
            userId = apiComment.userId,
            comment = apiComment.comment,
            rating = apiComment.rating
        )
    }
}
