package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddObservationUseCase @Inject constructor(
    private val observationRepository: IObservationRepository
) : ApiUseCaseParams<AddObservationUseCase.Params, Observation> {

    override suspend fun execute(params: Params): Flow<Resource<Observation>> =
        observationRepository.addObservation(params)

    data class Params(
        val userId: Long,
        val name: String,
        val description: String,
        val date: String,
        val lecturerId: Long,
        val imageUri: String?,
        val token: String
    )

}