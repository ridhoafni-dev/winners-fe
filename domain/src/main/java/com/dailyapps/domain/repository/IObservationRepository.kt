package com.dailyapps.domain.repository

import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow

interface IObservationRepository {
    suspend fun getObservationsByUserIdByDate(params: GetObservationsByUserIdByDateUseCase.Params): Flow<Resource<List<Observation>>>
}