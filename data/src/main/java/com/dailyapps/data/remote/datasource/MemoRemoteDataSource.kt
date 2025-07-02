package com.dailyapps.data.remote.datasource

import com.dailyapps.apiresponse.AddMemoResponse
import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.MemoApiResponse
import com.dailyapps.data.mapper.AddMemoMapper
import com.dailyapps.data.mapper.MemoDetailMapper
import com.dailyapps.data.mapper.MemoMapper
import com.dailyapps.data.remote.service.MemoService
import com.dailyapps.data.utils.TokenInterceptor
import com.dailyapps.entity.Memo
import javax.inject.Inject

/**
 * Remote data source for memo related operations
 */
class MemoRemoteDataSource @Inject constructor(
    private val memoService: MemoService,
    private val memoMapper: MemoMapper,
    private val memoDetailMapper: MemoDetailMapper,
    private val addMemoMapper: AddMemoMapper,
    private val tokenInterceptor: TokenInterceptor
) {
    /**
     * Get memos by user ID and date range
     */
    suspend fun getMemosByUserIdByDate(
        userId: Long,
        startDate: String,
        endDate: String,
        lecturer: Boolean,
    ): List<Memo> {
        val token = tokenInterceptor.getToken()
        val response: BaseResponse<List<MemoApiResponse>> = memoService.getMemosByUserIdByDate(
            token = token,
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            lecturer = lecturer
        )
        return memoMapper.mapFromApiResponse(response)
    }

    /**
     * Get memo by ID
     */
    suspend fun getMemoById(id: Long): Memo {
        val token = tokenInterceptor.getToken()
        val response: BaseResponse<MemoApiResponse> = memoService.getMemoById(
            token = token,
            id = id
        )
        return memoDetailMapper.mapFromApiResponse(response)
    }

    /**
     * Add a new memo
     */
    suspend fun addMemo(
        userId: Long,
        title: String,
        startDate: String,
        endDate: String,
        active: Boolean,
        status: String,
        lecturerId: Long
    ): Memo {
        val token = tokenInterceptor.getToken()
        val response: BaseResponse<AddMemoResponse> = memoService.addMemo(
            token = token,
            userId = userId,
            title = title,
            startDate = startDate,
            endDate = endDate,
            active = active,
            status = status,
            lecturerId = lecturerId
        )
        return addMemoMapper.mapFromApiResponse(response)
    }

    /**
     * Update an existing memo
     */
    suspend fun updateMemo(
        id: Long,
        title: String,
        startDate: String,
        endDate: String,
        active: Boolean,
        status: String
    ): Memo {
        val token = tokenInterceptor.getToken()
        val response: BaseResponse<MemoApiResponse> = memoService.updateMemo(
            token = token,
            id = id,
            title = title,
            startDate = startDate,
            endDate = endDate,
            active = active,
            status = status
        )
        return memoDetailMapper.mapFromApiResponse(response)
    }
}
