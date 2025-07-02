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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MemoRepository @Inject constructor(
    private val remoteDataSource: MemoRemoteDataSource
) : IMemoRepository {
    override suspend fun getMemosByUserIdByDate(params: GetMemosByUserIdByDateUseCase.Params): Flow<Resource<List<Memo>>> = flow {
        emit(Resource.Loading())
        try {
            val response = remoteDataSource.getMemosByUserIdByDate(
                userId = params.userId,
                startDate = params.startDate,
                endDate = params.endDate,
                lecturer = params.lecturer
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getMemoById(params: GetMemoByIdUseCase.Params): Flow<Resource<Memo>> = flow {
        emit(Resource.Loading())
        try {
            val response = remoteDataSource.getMemoById(params.id)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addMemo(params: AddMemoUseCase.Params): Flow<Resource<Memo>> = flow {
        emit(Resource.Loading())
        try {
            val response = remoteDataSource.addMemo(
                userId = params.userId,
                title = params.title,
                startDate = params.startDate,
                endDate = params.endDate,
                active = params.active,
                status = params.status,
                lecturerId = params.lecturerId
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateMemo(params: UpdateMemoUseCase.Params): Flow<Resource<Memo>> = flow {
        emit(Resource.Loading())
        try {
            val response = remoteDataSource.updateMemo(
                id = params.id,
                title = params.title,
                startDate = params.startDate,
                endDate = params.endDate,
                active = params.active,
                status = params.status
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addMemoComment(params: AddMemoCommentUseCase.Params): Flow<Resource<MemoComment>> = flow {
        emit(Resource.Loading())
        try {
            // This would need implementation in the remote data source
            // Placeholder for now
            emit(Resource.Error("Not implemented yet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
