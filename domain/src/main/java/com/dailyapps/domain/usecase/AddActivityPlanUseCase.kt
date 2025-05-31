package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IActivityPlanRepository
import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddActivityPlanUseCase @Inject constructor(
    private val activityPlanRepository: IActivityPlanRepository
) : ApiUseCaseParams<AddActivityPlanUseCase.Params, ActivityPlan> {

    override suspend fun execute(params: Params): Flow<Resource<ActivityPlan>> =
        activityPlanRepository.addActivityPlan(params)

    data class Params(
        val userId: Long,
        val name: String,
        val startDate: String,
        val endDate: String,
        val lecturerId: Long,
        val token: String
    )

}