package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IMemoRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Memo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddMemoUseCase @Inject constructor(private val repository: IMemoRepository) {
    suspend operator fun invoke(params: Params): Flow<Resource<Memo>> {
        return repository.addMemo(params)
    }

    data class Params(
        val token: String,
        val userId: Long,
        val title: String,
        val lecturerId: Long
    )
}
