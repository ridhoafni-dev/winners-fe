package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IActivityPlanRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ActivityPlan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActivityPlansByUserIdByDateUseCase @Inject constructor(
    private val repository: IActivityPlanRepository
): ApiUseCaseParams<GetActivityPlansByUserIdByDateUseCase.Params, List<ActivityPlan>> {
    override suspend fun execute(params: Params): Flow<Resource<List<ActivityPlan>>> =
        repository.getActivityPlansByUserIdByDate(params)

    data class Params(val userId: Long, val startDate: String, val endDate: String, val lecturer: Boolean, val token: String)

}