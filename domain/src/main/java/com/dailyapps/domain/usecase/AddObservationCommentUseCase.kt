package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ObservationComments
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddObservationCommentUseCase @Inject constructor(
    private val repository: IObservationRepository
) {
    suspend fun execute(params: Params): Flow<Resource<ObservationComments>> {
        return repository.addObservationComment(params)
    }

    data class Params(
        val id: Long,
        val userId: Long,
        val rating: Int,
        val comment: String,
        val token: String
    )
}
