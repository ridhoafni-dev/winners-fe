package com.dailyapps.data.repository

import com.dailyapps.data.remote.datasource.ObservationRemoteDataSource
import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservationRepository @Inject constructor(
        private val remoteDataSource: ObservationRemoteDataSource
) : IObservationRepository {
    override suspend fun getObservationsByUserIdByDate(params: GetObservationsByUserIdByDateUseCase.Params): Flow<Resource<List<Observation>>> =
        remoteDataSource.getObservationsByUserIdByDate(params)

}