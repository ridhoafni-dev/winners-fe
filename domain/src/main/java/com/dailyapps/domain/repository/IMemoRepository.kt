package com.dailyapps.domain.repository

import com.dailyapps.domain.usecase.AddMemoCommentUseCase
import com.dailyapps.domain.usecase.AddMemoUseCase
import com.dailyapps.domain.usecase.GetMemoByIdUseCase
import com.dailyapps.domain.usecase.GetMemosByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateMemoUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Memo
import com.dailyapps.entity.MemoComment
import kotlinx.coroutines.flow.Flow

interface IMemoRepository {
    suspend fun getMemosByUserIdByDate(params: GetMemosByUserIdByDateUseCase.Params): Flow<Resource<List<Memo>>>
    suspend fun getMemoById(params: GetMemoByIdUseCase.Params): Flow<Resource<Memo>>
    suspend fun addMemo(params: AddMemoUseCase.Params): Flow<Resource<Memo>>
    suspend fun updateMemo(params: UpdateMemoUseCase.Params): Flow<Resource<Memo>>
    suspend fun addMemoComment(params: AddMemoCommentUseCase.Params): Flow<Resource<MemoComment>>
}
