package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ObservationApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.Observation
import com.dailyapps.entity.ObservationComments
import com.dailyapps.entity.User
import javax.inject.Inject

class ObservationMapper @Inject constructor(): Mapper<BaseResponse<List<ObservationApiResponse>>, List<Observation>> {
    override fun mapFromApiResponse(type: BaseResponse<List<ObservationApiResponse>>): List<Observation> {
        return type.data.map { data ->
            Observation(
                date = data.date,
                image = data.image,
                name = data.name,
                observationComments = data.observationComments?.let {
                    ObservationComments(
                        observationId = it.observationId,
                        rating = it.rating,
                        comment = it.comment,
                        id = it.id,
                        userId = it.userId
                    )
                },
                description = data.description,
                active = data.active,
                id = data.id,
                userId = data.userId,
                user = data.user?.let {
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