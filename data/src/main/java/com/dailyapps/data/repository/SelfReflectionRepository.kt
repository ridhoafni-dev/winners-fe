package com.dailyapps.data.repository

import com.dailyapps.data.remote.datasource.SelfReflectionRemoteDataSource
import com.dailyapps.domain.repository.ISelfReflectionRepository
import com.dailyapps.domain.usecase.AddSelfReflectionCommentUseCase
import com.dailyapps.domain.usecase.AddSelfReflectionUseCase
import com.dailyapps.domain.usecase.GetSelfReflectionByIdUseCase
import com.dailyapps.domain.usecase.GetSelfReflectionsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateSelfReflectionUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.SelfReflection
import com.dailyapps.entity.SelfReflectionComment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SelfReflectionRepository @Inject constructor(
    private val selfReflectionRemoteDataSource: SelfReflectionRemoteDataSource
) : ISelfReflectionRepository{
    override suspend fun getSelfReflectionsByUserIdByDate(params: GetSelfReflectionsByUserIdByDateUseCase.Params): Flow<Resource<List<SelfReflection>>> =
        selfReflectionRemoteDataSource.getSelfReflectionsByUserIdByDate(params)

    override suspend fun getSelfReflectionById(params: GetSelfReflectionByIdUseCase.Params): Flow<Resource<SelfReflection>> =
        selfReflectionRemoteDataSource.getSelfReflectionById(params)

    override suspend fun addSelfReflection(params: AddSelfReflectionUseCase.Params): Flow<Resource<SelfReflection>> =
        selfReflectionRemoteDataSource.addSelfReflection(params)

    override suspend fun updateSelfReflection(params: UpdateSelfReflectionUseCase.Params): Flow<Resource<SelfReflection>> =
        selfReflectionRemoteDataSource.updateSelfReflection(params)

    override suspend fun addSelfReflectionComment(params: AddSelfReflectionCommentUseCase.Params): Flow<Resource<SelfReflectionComment>> =
        selfReflectionRemoteDataSource.addSelfReflectionComment(params)

}