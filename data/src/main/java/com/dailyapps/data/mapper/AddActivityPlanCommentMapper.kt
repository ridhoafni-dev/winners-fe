package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.ActivityPlanCommentResponse
import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.ActivityPlanComment
import javax.inject.Inject

class AddActivityPlanCommentMapper @Inject constructor() : Mapper<BaseResponse<ActivityPlanCommentResponse>, ActivityPlanComment> {
    override fun mapFromApiResponse(type: BaseResponse<ActivityPlanCommentResponse>): ActivityPlanComment {
        val data = type.data
        return ActivityPlanComment(
            id = data.id,
            activityPlanId = data.activityPlanId,
            userId = data.userId,
            rating = data.rating,
            comment = data.comment,
        )
    }

}
