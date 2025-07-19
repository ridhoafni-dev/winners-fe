package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.ISelfReflectionRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.SelfReflection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSelfReflectionsByUserIdByDateUseCase @Inject constructor(private val repository: ISelfReflectionRepository) {
    suspend operator fun invoke(params: Params): Flow<Resource<List<SelfReflection>>> {
        return repository.getSelfReflectionsByUserIdByDate(params)
    }

    data class Params(
        val token: String,
        val userId: Long,
        val startDate: String,
        val endDate: String,
        val lecturer: Boolean
    )
}
