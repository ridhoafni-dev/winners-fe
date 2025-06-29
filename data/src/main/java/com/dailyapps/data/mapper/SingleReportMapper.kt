
package com.dailyapps.data .mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ReportApiResponse
import com.dailyapps.data.utils.Mapper
import com.dailyapps.entity.Report
import com.dailyapps.entity.ReportLecturer
import javax.inject.Inject

class SingleReportMapper @Inject constructor() :
    Mapper<BaseResponse<ReportApiResponse>, Report> {
    override fun mapFromApiResponse(type: BaseResponse<ReportApiResponse>): Report {
        val response = type.data
        return Report(
            date = response.date,
            image = response.image,
            id = response.id,
            createAt = response.date,
            reportLecturer = ReportLecturer(
                reportId = response.reportLecturer?.reportId,
                id = response.reportLecturer?.id,
                userId = response.reportLecturer?.userId
            ),
        )
    }
}