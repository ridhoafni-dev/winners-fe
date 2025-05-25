package com.dailyapps.data.remote.datasource

import com.dailyapps.common.utils.formatToken
import com.dailyapps.data.mapper.ObservationDetailMapper
import com.dailyapps.data.mapper.ObservationMapper
import com.dailyapps.data.remote.service.ObservationService
import com.dailyapps.data.utils.apiCall
import com.dailyapps.data.utils.mapFromApiResponse
import com.dailyapps.domain.usecase.GetObservationByIdUseCase
import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObservationRemoteDataSource @Inject constructor(
    private val observationService: ObservationService,
    private val observationMapper: ObservationMapper,
    private val observationDetailMapper: ObservationDetailMapper,
){
    suspend fun getObservationsByUserIdByDate(params: GetObservationsByUserIdByDateUseCase.Params) : Flow<Resource<List<Observation>>> {
        return mapFromApiResponse(
            result = apiCall {
                observationService.getObservationsByUserIdByDate(params.token.formatToken(), params.userId, params.startDate, params.endDate, params.lecturer)
            }, observationMapper
        )
    }
    suspend fun getObservationById(params: GetObservationByIdUseCase.Params) : Flow<Resource<Observation>> {
        return mapFromApiResponse(
            result = apiCall {
                observationService.getObservationById(params.token.formatToken(), params.id)
            }, observationDetailMapper
        )
    }
}