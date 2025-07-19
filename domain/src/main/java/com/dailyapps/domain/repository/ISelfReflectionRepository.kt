package com.dailyapps.domain.repository

import com.dailyapps.domain.usecase.AddSelfReflectionCommentUseCase
import com.dailyapps.domain.usecase.AddSelfReflectionUseCase
import com.dailyapps.domain.usecase.GetSelfReflectionByIdUseCase
import com.dailyapps.domain.usecase.GetSelfReflectionsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateSelfReflectionUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.SelfReflection
import com.dailyapps.entity.SelfReflectionComment
import kotlinx.coroutines.flow.Flow

interface ISelfReflectionRepository {
    suspend fun getSelfReflectionsByUserIdByDate(params: GetSelfReflectionsByUserIdByDateUseCase.Params): Flow<Resource<List<SelfReflection>>>
    suspend fun getSelfReflectionById(params: GetSelfReflectionByIdUseCase.Params): Flow<Resource<SelfReflection>>
    suspend fun addSelfReflection(params: AddSelfReflectionUseCase.Params): Flow<Resource<SelfReflection>>
    suspend fun updateSelfReflection(params: UpdateSelfReflectionUseCase.Params): Flow<Resource<SelfReflection>>
    suspend fun addSelfReflectionComment(params: AddSelfReflectionCommentUseCase.Params): Flow<Resource<SelfReflectionComment>>
}
