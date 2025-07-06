package com.dailyapps.data.remote.datasource

import com.dailyapps.common.utils.formatToken
import com.dailyapps.data.mapper.AddMemoMapper
import com.dailyapps.data.mapper.MemoDetailMapper
import com.dailyapps.data.mapper.MemoMapper
import com.dailyapps.data.remote.service.MemoService
import com.dailyapps.data.utils.apiCall
import com.dailyapps.data.utils.mapFromApiResponse
import com.dailyapps.domain.usecase.AddMemoUseCase
import com.dailyapps.domain.usecase.GetMemoByIdUseCase
import com.dailyapps.domain.usecase.GetMemosByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateMemoUseCase
import com.dailyapps.domain.usecase.AddMemoCommentUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Memo
import com.dailyapps.entity.MemoComment
import com.dailyapps.data.mapper.AddMemoCommentMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for memo related operations
 */
@Singleton
class MemoRemoteDataSource @Inject constructor(
    private val memoService: MemoService,
    private val memoMapper: MemoMapper,
    private val memoDetailMapper: MemoDetailMapper,
    private val addMemoMapper: AddMemoMapper,
    private val addMemoCommentMapper: AddMemoCommentMapper,
) {
    /**
     * Get memos by user ID and date range
     */
    suspend fun getMemosByUserIdByDate(params: GetMemosByUserIdByDateUseCase.Params): Flow<Resource<List<Memo>>> {
        return mapFromApiResponse(
            result = apiCall {
                memoService.getMemosByUserIdByDate(
                    params.token.formatToken(),
                    params.userId,
                    params.startDate,
                    params.endDate,
                    params.lecturer
                )
            }, memoMapper
        )
    }

    /**
     * Get memo by ID
     */
    suspend fun getMemoById(params: GetMemoByIdUseCase.Params): Flow<Resource<Memo>> {
        return mapFromApiResponse(
            result = apiCall {
                memoService.getMemoById(
                    params.token.formatToken(),
                    params.id
                )
            }, memoDetailMapper
        )
    }

    /**
     * Add a new memo
     */
    suspend fun addMemo(params: AddMemoUseCase.Params): Flow<Resource<Memo>> {
        return mapFromApiResponse(
            result = apiCall {
                memoService.addMemo(
                    token = params.token.formatToken(),
                    userId = params.userId,
                    title = params.title,
                    lecturerId = params.lecturerId
                )
            }, addMemoMapper
        )
    }

    /**
     * Update an existing memo
     */
    suspend fun updateMemo(params: UpdateMemoUseCase.Params): Flow<Resource<Memo>> {
        return mapFromApiResponse(
            result = apiCall {
                memoService.updateMemo(
                    token = params.token.formatToken(),
                    id = params.id,
                    userId = params.userId,
                    title = params.title,
                    lecturerId = params.lecturerId                )
            }, memoDetailMapper
        )
    }

    /**
     * Add a comment to a memo
     */
    suspend fun addMemoComment(params: AddMemoCommentUseCase.Params): Flow<Resource<MemoComment>> {
        return mapFromApiResponse(
            result = apiCall {
                memoService.addMemoComment(
                    token = params.token.formatToken(),
                    id = params.id,
                    userId = params.userId,
                    rating = params.rating,
                    comment = params.comment
                )
            }, addMemoCommentMapper
        )
    }
}
