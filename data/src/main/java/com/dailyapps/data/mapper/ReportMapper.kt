package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ReportApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.Report
import javax.inject.Inject

class ReportMapper @Inject constructor() :
    Mapper<BaseResponse<List<ReportApiResponse>>, List<Report>> {
    override fun mapFromApiResponse(type: BaseResponse<List<ReportApiResponse>>): List<Report> {

        return type.data.mapNotNull { response ->
            try {
                // Using only id and date fields that seem to work
                Report(
                    date = response.date,
                    image = response.image,
                    active = response.active,
                    id = response.id,
                    userId = response.userId,
                    createAt = response.createAt,
                    updatedAt = response.updatedAt,
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
