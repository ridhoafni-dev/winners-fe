package com.dailyapps.domain.repository

import com.dailyapps.domain.usecase.AddObservationUseCase
import com.dailyapps.domain.usecase.GetObservationByIdUseCase
import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateObservationUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow

interface IObservationRepository {
    suspend fun getObservationsByUserIdByDate(params: GetObservationsByUserIdByDateUseCase.Params): Flow<Resource<List<Observation>>>
    suspend fun getObservationById(params: GetObservationByIdUseCase.Params): Flow<Resource<Observation>>
    suspend fun addObservation(params: AddObservationUseCase.Params): Flow<Resource<Observation>>
    suspend fun updateObservation(params: UpdateObservationUseCase.Params): Flow<Resource<Observation>>
}