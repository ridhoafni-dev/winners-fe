package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.ISelfReflectionRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.SelfReflectionComment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddSelfReflectionCommentUseCase @Inject constructor(private val repository: ISelfReflectionRepository) {
    suspend operator fun invoke(params: Params): Flow<Resource<SelfReflectionComment>> {
        return repository.addSelfReflectionComment(params)
    }

    data class Params(
        val token: String,
        val reflectionId: Long,
        val userId: Long,
        val rating: Int,
        val comment: String
    )
}
