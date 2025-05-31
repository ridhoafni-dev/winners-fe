package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IActivityPlanRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ActivityPlanComment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddActivityPlanCommentUseCase @Inject constructor(
    private val repository: IActivityPlanRepository
) {
    suspend fun execute(params: Params): Flow<Resource<ActivityPlanComment>> {
        return repository.addActivityPlanComment(params)
    }

    data class Params(
        val id: Long,
        val userId: Long,
        val rating: Int,
        val comment: String,
        val token: String
    )
}
