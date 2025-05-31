package com.dailyapps.data.repository

import com.dailyapps.data.remote.datasource.ActivityPlanRemoteDataSource
import com.dailyapps.domain.repository.IActivityPlanRepository
import com.dailyapps.domain.usecase.AddActivityPlanUseCase
import com.dailyapps.domain.usecase.GetActivityPlanByIdUseCase
import com.dailyapps.domain.usecase.GetActivityPlansByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateActivityPlanUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ActivityPlan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityPlanRepository @Inject constructor(
        private val remoteDataSource: ActivityPlanRemoteDataSource
) : IActivityPlanRepository {
    override suspend fun getActivityPlansByUserIdByDate(params: GetActivityPlansByUserIdByDateUseCase.Params): Flow<Resource<List<ActivityPlan>>> =
        remoteDataSource.getActivityPlansByUserIdByDate(params)

    override suspend fun getActivityPlanById(params: GetActivityPlanByIdUseCase.Params): Flow<Resource<ActivityPlan>> =
        remoteDataSource.getActivityPlanById(params)

    override suspend fun addActivityPlan(params: AddActivityPlanUseCase.Params): Flow<Resource<ActivityPlan>> =
        remoteDataSource.addActivityPlan(params)


    override suspend fun updateActivityPlan(params: UpdateActivityPlanUseCase.Params): Flow<Resource<ActivityPlan>> =
        remoteDataSource.updateActivityPlan(params)

}