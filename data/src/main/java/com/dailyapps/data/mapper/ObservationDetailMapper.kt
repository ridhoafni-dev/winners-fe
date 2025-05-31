package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ObservationApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.Observation
import com.dailyapps.entity.ObservationComments
import com.dailyapps.entity.ObservationLecturer
import com.dailyapps.entity.User
import javax.inject.Inject

class ObservationDetailMapper @Inject constructor(): Mapper<BaseResponse<ObservationApiResponse>, Observation> {
    override fun mapFromApiResponse(type: BaseResponse<ObservationApiResponse>): Observation {
        return type.data.let { data ->
            Observation(
                date = data.date,
                image = data.image,
                name = data.name,
                observationComments = data.observationCommentsApiResponse?.let {
                    ObservationComments(
                        observationId = it.observationId,
                        rating = it.rating,
                        comment = it.comment,
                        id = it.id,
                        userId = it.userId
                    )
                },
                observationLecturer = data.observationLecturer?.let {
                    ObservationLecturer(
                        userId = it.userId,
                    )
                },
                description = data.description,
                active = data.active,
                id = data.id,
                userId = data.userId,
                user = data.userApiResponse?.let {
                    User(
                        id = it.id,
                        email = it.email
                    )
                },
                createAt = data.createAt,
                updatedAt = data.updatedAt
            )
        }

    }

}