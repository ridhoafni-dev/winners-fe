package com.dailyapps.domain.repository

import com.dailyapps.domain.usecase.AddActivityPlanCommentUseCase
import com.dailyapps.domain.usecase.AddActivityPlanUseCase
import com.dailyapps.domain.usecase.GetActivityPlanByIdUseCase
import com.dailyapps.domain.usecase.GetActivityPlansByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateActivityPlanUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.entity.ActivityPlanComment
import kotlinx.coroutines.flow.Flow

interface IActivityPlanRepository {
    suspend fun getActivityPlansByUserIdByDate(params: GetActivityPlansByUserIdByDateUseCase.Params): Flow<Resource<List<ActivityPlan>>>
    suspend fun getActivityPlanById(params: GetActivityPlanByIdUseCase.Params): Flow<Resource<ActivityPlan>>
    suspend fun addActivityPlan(params: AddActivityPlanUseCase.Params): Flow<Resource<ActivityPlan>>
    suspend fun updateActivityPlan(params: UpdateActivityPlanUseCase.Params): Flow<Resource<ActivityPlan>>
    suspend fun addActivityPlanComment(params: AddActivityPlanCommentUseCase.Params): Flow<Resource<ActivityPlanComment>>
}

