package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ObservationCommentsApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.ObservationComments
import javax.inject.Inject

class AddObservationCommentMapper @Inject constructor() : Mapper<BaseResponse<ObservationCommentsApiResponse>, ObservationComments> {
    override fun mapFromApiResponse(type: BaseResponse<ObservationCommentsApiResponse>): ObservationComments {
        val data = type.data
        return ObservationComments(
            id = data.id,
            observationId = data.observationId,
            userId = data.userId,
            rating = data.rating,
            comment = data.comment,
        )
    }

}
