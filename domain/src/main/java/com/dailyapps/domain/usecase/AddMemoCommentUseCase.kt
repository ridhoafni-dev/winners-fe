package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IMemoRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.MemoComment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddMemoCommentUseCase @Inject constructor(private val repository: IMemoRepository) {
    suspend operator fun invoke(params: Params): Flow<Resource<MemoComment>> {
        return repository.addMemoComment(params)
    }

    data class Params(
        val token: String,
        val id: Long,
        val userId: Long,
        val comment: String,
        val rating: Int
    )
}
