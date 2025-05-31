package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ActivityPlanApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.entity.ActivityPlanComment
import com.dailyapps.entity.ActivityPlanLecturer
import com.dailyapps.entity.User
import javax.inject.Inject

class ActivityPlanDetailMapper @Inject constructor(): Mapper<BaseResponse<ActivityPlanApiResponse>, ActivityPlan> {
    override fun mapFromApiResponse(type: BaseResponse<ActivityPlanApiResponse>): ActivityPlan {
        val response = type.data
        return ActivityPlan(
            id = response.id,
            name = response.name,
            active = response.active,
            userId = response.userId,
            endDate = response.endDate,
            startDate = response.startDate,
            createAt = response.createAt,
            status = response.status,
            updatedAt = response.updatedAt,
            user = mapUser(response.user),
            activityPlanLecturer = mapActivityPlanLecturer(response.activityPlanLecturer),
            activityPlanComment = mapActivityPlanComment(response.activityPlanCommentResponse)
        )
    }

    private fun mapUser(apiUser: com.dailyapps.apiresponse.User?): User? {
        return if (apiUser == null) null
        else User(
            id = apiUser.id,
            email = apiUser.email,
            role = apiUser.role
        )
    }

    private fun mapActivityPlanLecturer(apiLecturer: com.dailyapps.apiresponse.ActivityPlanLecturer?): ActivityPlanLecturer? {
        return if (apiLecturer == null) null
        else ActivityPlanLecturer(
            userId = apiLecturer.userId
        )
    }

    private fun mapActivityPlanComment(apiComment: com.dailyapps.apiresponse.ActivityPlanCommentResponse?): ActivityPlanComment? {
        return if (apiComment == null) null
        else ActivityPlanComment(
            id = apiComment.id,
            rating = apiComment.rating,
            activityPlanId = apiComment.activityPlanId,
            comment = apiComment.comment,
            userId = apiComment.userId
        )
    }
}
