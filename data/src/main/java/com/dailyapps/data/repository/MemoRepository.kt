package com.dailyapps.data.repository

import com.dailyapps.data.remote.datasource.MemoRemoteDataSource
import com.dailyapps.domain.repository.IMemoRepository
import com.dailyapps.domain.usecase.AddMemoCommentUseCase
import com.dailyapps.domain.usecase.AddMemoUseCase
import com.dailyapps.domain.usecase.GetMemoByIdUseCase
import com.dailyapps.domain.usecase.GetMemosByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateMemoUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Memo
import com.dailyapps.entity.MemoComment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemoRepository @Inject constructor(
    private val remoteDataSource: MemoRemoteDataSource
) : IMemoRepository {
    override suspend fun getMemosByUserIdByDate(params: GetMemosByUserIdByDateUseCase.Params): Flow<Resource<List<Memo>>> =
        remoteDataSource.getMemosByUserIdByDate(params)

    override suspend fun getMemoById(params: GetMemoByIdUseCase.Params): Flow<Resource<Memo>> =
        remoteDataSource.getMemoById(params)

    override suspend fun addMemo(params: AddMemoUseCase.Params): Flow<Resource<Memo>> =
        remoteDataSource.addMemo(params)

    override suspend fun updateMemo(params: UpdateMemoUseCase.Params): Flow<Resource<Memo>> =
        remoteDataSource.updateMemo(params)

    override suspend fun addMemoComment(params: AddMemoCommentUseCase.Params): Flow<Resource<MemoComment>> =
        remoteDataSource.addMemoComment(params)
}
