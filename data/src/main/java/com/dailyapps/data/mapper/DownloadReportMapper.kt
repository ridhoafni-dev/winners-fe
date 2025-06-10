package com.dailyapps.data.mapper

import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.data.utils.Mapper
import javax.inject.Inject

class DownloadReportMapper @Inject constructor() : Mapper<BaseResponse<String>, String> {
    override fun mapFromApiResponse(type: BaseResponse<String>): String {
        return type.data
    }
}