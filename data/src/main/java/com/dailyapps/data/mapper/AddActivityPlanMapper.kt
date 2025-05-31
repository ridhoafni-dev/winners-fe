package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ActivityPlanApiResponse
import com.dailyapps.apiresponse.AddActivityPlanResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.entity.ActivityPlanLecturer
import com.dailyapps.entity.User
import javax.inject.Inject

/**
 * Mapper for converting AddActivityPlanApiResponse to ActivityPlan entity
 * Used when creating a new activity plan
 */
class AddActivityPlanMapper @Inject constructor() : Mapper<BaseResponse<AddActivityPlanResponse>, ActivityPlan> {
    override fun mapFromApiResponse(type: BaseResponse<AddActivityPlanResponse>): ActivityPlan {
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
        )
    }
}
