package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.utils.ApiUseCaseParams
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetObservationsByUserIdByDateUseCase @Inject constructor(
    private val repository: IObservationRepository
): ApiUseCaseParams<GetObservationsByUserIdByDateUseCase.Params, List<Observation>> {
    override suspend fun execute(params: Params): Flow<Resource<List<Observation>>> =
        repository.getObservationsByUserIdByDate(params)

    data class Params(val userId: Long, val startDate: String, val endDate: String, val lecturer: Boolean, val token: String)

}