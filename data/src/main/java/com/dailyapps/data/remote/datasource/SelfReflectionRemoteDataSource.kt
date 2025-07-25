package com.dailyapps.data.remote.datasource

import com.dailyapps.common.utils.formatToken
import com.dailyapps.data.mapper.AddSelfReflectionCommentMapper
import com.dailyapps.data.mapper.SelfReflectionMapper
import com.dailyapps.data.mapper.SelfReflectionDetailMapper
import com.dailyapps.data.mapper.AddSelfReflectionMapper
import com.dailyapps.data.remote.service.SelfReflectionService
import com.dailyapps.data.utils.apiCall
import com.dailyapps.data.utils.mapFromApiResponse
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
import javax.inject.Singleton

/**
 * Remote data source for self reflection related operations
 */
@Singleton
class SelfReflectionRemoteDataSource @Inject constructor(
    private val selfReflectionService: SelfReflectionService,
    private val selfReflectionMapper: SelfReflectionMapper,
    private val selfReflectionDetailMapper: SelfReflectionDetailMapper,
    private val addSelfReflectionMapper: AddSelfReflectionMapper,
    private val addSelfReflectionCommentMapper: AddSelfReflectionCommentMapper,
) {
    /**
     * Get self reflections by user ID and date range
     */
    suspend fun getSelfReflectionsByUserIdByDate(params: GetSelfReflectionsByUserIdByDateUseCase.Params): Flow<Resource<List<SelfReflection>>> {
        return mapFromApiResponse(
            result = apiCall {
                selfReflectionService.getSelfReflectionsByUserIdByDate(
                    params.token.formatToken(),
                    params.userId,
                    params.startDate,
                    params.endDate,
                    params.lecturer
                )
            }, selfReflectionMapper
        )
    }

    /**
     * Get self reflection by ID
     */
    suspend fun getSelfReflectionById(params: GetSelfReflectionByIdUseCase.Params): Flow<Resource<SelfReflection>> {
        return mapFromApiResponse(
            result = apiCall {
                selfReflectionService.getSelfReflectionById(
                    params.token.formatToken(),
                    params.id
                )
            }, selfReflectionDetailMapper
        )
    }

    /**
     * Add a new self reflection
     */
    suspend fun addSelfReflection(params: AddSelfReflectionUseCase.Params): Flow<Resource<SelfReflection>> {

        return mapFromApiResponse(
            result = apiCall {
                selfReflectionService.addSelfReflection(
                    token = params.token.formatToken(),
                    userId = params.userId,
                    lecturerId = params.lecturerId,
                    description = params.title
                )
            }, addSelfReflectionMapper
        )
    }

    /**
     * Update an existing self reflection
     */
    suspend fun updateSelfReflection(params: UpdateSelfReflectionUseCase.Params): Flow<Resource<SelfReflection>> {

        return mapFromApiResponse(
            result = apiCall {
                selfReflectionService.updateSelfReflection(
                    token = params.token.formatToken(),
                    id = params.id,
                    userId = params.userId,
                    lecturerId = params.lecturerId,
                    description = params.title
                )
            }, selfReflectionDetailMapper
        )
    }

    /**
     * Add a comment to a self reflection
     */
    suspend fun addSelfReflectionComment(params: AddSelfReflectionCommentUseCase.Params): Flow<Resource<SelfReflectionComment>> {
        return mapFromApiResponse(
            result = apiCall {
                selfReflectionService.addSelfReflectionComment(
                    token = params.token.formatToken(),
                    id = params.reflectionId,
                    userId = params.userId,
                    rating = params.rating,
                    comment = params.comment
                )
            }, addSelfReflectionCommentMapper
        )
    }
}
