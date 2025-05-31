package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ActivityPlanApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.entity.ActivityPlanComment
import com.dailyapps.entity.ActivityPlanLecturer
import com.dailyapps.entity.User
import javax.inject.Inject

class ActivityPlanMapper @Inject constructor(): Mapper<BaseResponse<List<ActivityPlanApiResponse>>, List<ActivityPlan>> {
    override fun mapFromApiResponse(type: BaseResponse<List<ActivityPlanApiResponse>>): List<ActivityPlan> {
        return type.data.map { response ->
            ActivityPlan(
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
                activityPlanComment = mapActivityPlanComment(response.activityPlanComment)
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

    private fun mapActivityPlanLecturer(apiLecturer: com.dailyapps.apiresponse.ActivityPlanLecturer?): ActivityPlanLecturer? {
        if (apiLecturer == null) return null
        
        return ActivityPlanLecturer(
            userId = apiLecturer.userId
        )
    }

    private fun mapActivityPlanComment(apiComment: com.dailyapps.apiresponse.ActivityPlanComment?): ActivityPlanComment? {
        if (apiComment == null) return null
        
        return ActivityPlanComment(
            id = apiComment.id,
            rating = apiComment.rating,
            activityPlanId = apiComment.activityPlanId,
            comment = apiComment.comment,
            userId = apiComment.userId
        )
    }
}
