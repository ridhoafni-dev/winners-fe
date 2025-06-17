package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetObservationByIdUseCase @Inject constructor(
    private val repository: IObservationRepository
): ApiUseCaseParams<GetObservationByIdUseCase.Params, Observation> {
    override suspend fun execute(params: Params): Flow<Resource<Observation>> =
        repository.getObservationById(params)

    data class Params(val id: Long, val token: String)

}