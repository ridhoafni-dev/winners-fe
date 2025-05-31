package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.AddObservationResponse
import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.Observation
import javax.inject.Inject

class AddObservationMapper @Inject constructor(): Mapper<BaseResponse<AddObservationResponse>, Observation> {
    override fun mapFromApiResponse(type: BaseResponse<AddObservationResponse>): Observation {
        return type.data.let { data ->
            Observation(
                date = data.date,
                image = data.image,
                name = data.name,
                description = data.description,
                active = data.active,
                id = data.id,
                userId = data.userId,
                createAt = data.createAt,
                updatedAt = data.updatedAt
            )
        }

    }

}