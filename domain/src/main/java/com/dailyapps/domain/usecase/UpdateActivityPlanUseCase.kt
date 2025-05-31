package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IActivityPlanRepository
import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateActivityPlanUseCase @Inject constructor(
    private val activityPlanRepository: IActivityPlanRepository
) : ApiUseCaseParams<UpdateActivityPlanUseCase.Params, ActivityPlan> {

    override suspend fun execute(params: Params): Flow<Resource<ActivityPlan>> =
        activityPlanRepository.updateActivityPlan(params)

    data class Params(
        val id: Long,
        val userId: Long,
        val name: String,
        val startDate: String,
        val endDate: String,
        val lecturerId: Long,
        val token: String
    )

}