package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IActivityPlanRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ActivityPlan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActivityPlanByIdUseCase @Inject constructor(
    private val repository: IActivityPlanRepository
): ApiUseCaseParams<GetActivityPlanByIdUseCase.Params, ActivityPlan> {
    override suspend fun execute(params: Params): Flow<Resource<ActivityPlan>> =
        repository.getActivityPlanById(params)

    data class Params(val id: Long, val token: String)

}