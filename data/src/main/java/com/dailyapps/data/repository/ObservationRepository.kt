package com.dailyapps.data.repository

import com.dailyapps.data.remote.datasource.ObservationRemoteDataSource
import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.usecase.AddObservationCommentUseCase
import com.dailyapps.domain.usecase.AddObservationUseCase
import com.dailyapps.domain.usecase.GetObservationByIdUseCase
import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateObservationUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
import com.dailyapps.entity.ObservationComments
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservationRepository @Inject constructor(
        private val remoteDataSource: ObservationRemoteDataSource
) : IObservationRepository {
    override suspend fun getObservationsByUserIdByDate(params: GetObservationsByUserIdByDateUseCase.Params): Flow<Resource<List<Observation>>> =
        remoteDataSource.getObservationsByUserIdByDate(params)

    override suspend fun getObservationById(params: GetObservationByIdUseCase.Params): Flow<Resource<Observation>> =
        remoteDataSource.getObservationById(params)

    override suspend fun addObservation(params: AddObservationUseCase.Params): Flow<Resource<Observation>> =
        remoteDataSource.addObservation(params)


    override suspend fun updateObservation(params: UpdateObservationUseCase.Params): Flow<Resource<Observation>> =
        remoteDataSource.updateObservation(params)

    override suspend fun addObservationComment(params: AddObservationCommentUseCase.Params): Flow<Resource<ObservationComments>> =
        remoteDataSource.addObservationComment(params)


}