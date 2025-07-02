package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IMemoRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Memo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateMemoUseCase @Inject constructor(private val repository: IMemoRepository) {
    suspend operator fun invoke(params: Params): Flow<Resource<Memo>> {
        return repository.updateMemo(params)
    }

    data class Params(
        val id: Long,
        val title: String,
        val startDate: String,
        val endDate: String,
        val active: Boolean,
        val status: String
    )
}
