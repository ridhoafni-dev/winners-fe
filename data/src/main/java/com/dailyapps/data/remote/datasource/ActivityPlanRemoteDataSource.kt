package com.dailyapps.data.remote.datasource

import android.content.Context
import android.net.Uri
import com.dailyapps.common.utils.formatToken
import com.dailyapps.data.mapper.AddActivityPlanMapper
import com.dailyapps.data.mapper.ActivityPlanDetailMapper
import com.dailyapps.data.mapper.ActivityPlanMapper
import com.dailyapps.data.remote.service.ActivityPlanService
import com.dailyapps.data.utils.apiCall
import com.dailyapps.data.utils.mapFromApiResponse
import com.dailyapps.domain.usecase.AddActivityPlanUseCase
import com.dailyapps.domain.usecase.GetActivityPlanByIdUseCase
import com.dailyapps.domain.usecase.GetActivityPlansByUserIdByDateUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ActivityPlan
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri
import com.dailyapps.data.mapper.AddActivityPlanCommentMapper
import com.dailyapps.domain.usecase.AddActivityPlanCommentUseCase
import com.dailyapps.domain.usecase.UpdateActivityPlanUseCase
import com.dailyapps.entity.ActivityPlanComment
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class ActivityPlanRemoteDataSource @Inject constructor(
    private val activityPlanService: ActivityPlanService,
    private val activityPlanMapper: ActivityPlanMapper,
    private val activityPlanDetailMapper: ActivityPlanDetailMapper,
    private val addActivityPlanMapper: AddActivityPlanMapper,
    private val addActivityPlanCommentMapper: AddActivityPlanCommentMapper,
){
    suspend fun getActivityPlansByUserIdByDate(params: GetActivityPlansByUserIdByDateUseCase.Params) : Flow<Resource<List<ActivityPlan>>> {
        return mapFromApiResponse(
            result = apiCall {
                activityPlanService.getActivityPlansByUserIdByDate(params.token.formatToken(), params.userId, params.startDate, params.endDate, params.lecturer)
            }, activityPlanMapper
        )
    }
    suspend fun getActivityPlanById(params: GetActivityPlanByIdUseCase.Params) : Flow<Resource<ActivityPlan>> {
        return mapFromApiResponse(
            result = apiCall {
                activityPlanService.getActivityPlanById(params.token.formatToken(), params.id)
            }, activityPlanDetailMapper
        )
    }

    suspend fun addActivityPlan(params: AddActivityPlanUseCase.Params): Flow<Resource<ActivityPlan>> {
        return mapFromApiResponse(
            result = apiCall {

                // Make the service call with all parameters
                activityPlanService.addActivityPlan(
                    token = params.token.formatToken(),
                    userId = params.userId,
                    name = params.name,
                    startDate = params.startDate,
                    endDate = params.endDate,
                    active = true,
                    status = "Aktif",
                    lecturerId = params.lecturerId
                )
            },
            addActivityPlanMapper
        )
    }
    
    suspend fun updateActivityPlan(params: UpdateActivityPlanUseCase.Params): Flow<Resource<ActivityPlan>> {
        return mapFromApiResponse(
            result = apiCall {

                // Make the service call with all parameters
                activityPlanService.updateActivityPlan(
                    token = params.token.formatToken(),
                    id = params.id,
                    userId = params.userId,
                    name = params.name,
                    startDate = params.startDate,
                    endDate = params.endDate,
                    active = true,
                    status = params.status,
                    lecturerId = params.lecturerId
                )
            },
            addActivityPlanMapper
        )
    }

    suspend fun addActivityPlanComment(params: AddActivityPlanCommentUseCase.Params): Flow<Resource<ActivityPlanComment>> {
        return mapFromApiResponse(
            result = apiCall {
                activityPlanService.addActivityPlanComment(
                    token = params.token.formatToken(),
                    id = params.id,
                    rating = params.rating,
                    comment = params.comment
                )
            },
            addActivityPlanCommentMapper
        )
    }
}
