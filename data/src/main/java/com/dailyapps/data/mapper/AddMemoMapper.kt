package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.AddMemoResponse
import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.Memo
import com.dailyapps.entity.MemoComment
import com.dailyapps.entity.MemoLecturer
import javax.inject.Inject

/**
 * Mapper for converting AddMemoResponse to Memo entity
 */
class AddMemoMapper @Inject constructor() : Mapper<BaseResponse<AddMemoResponse>, Memo> {
    override fun mapFromApiResponse(type: BaseResponse<AddMemoResponse>): Memo {
        val response = type.data
        return Memo(
            title = response.title,
            userId = response.userId,
        )
    }
}
