package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.ISelfReflectionRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.SelfReflection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddSelfReflectionUseCase @Inject constructor(private val repository: ISelfReflectionRepository) {
    suspend operator fun invoke(params: Params): Flow<Resource<SelfReflection>> {
        return repository.addSelfReflection(params)
    }

    data class Params(
        val token: String,
        val userId: Long,
        val title: String,
        val lecturerId: Long
    )
}
