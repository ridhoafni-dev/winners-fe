package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ReportApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.Report
import javax.inject.Inject

class ReportMapper @Inject constructor() :
    Mapper<BaseResponse<List<ReportApiResponse>>, List<Report>> {
    override fun mapFromApiResponse(type: BaseResponse<List<ReportApiResponse>>): List<Report> {
        return type.data?.map { response ->
            Report(
                id = response.id,
                name = response.name,
                documentUrl = response.documentUrl,
                date = response.date,
                status = response.status
            )
        } ?: emptyList()
    }
}
