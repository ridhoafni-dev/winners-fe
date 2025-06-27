package com.dailyapps.data.remote.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.FileUtils
import com.dailyapps.common.utils.formatToken
import com.dailyapps.data.mapper.DownloadReportMapper
import com.dailyapps.data.mapper.ReportMapper
import com.dailyapps.data.remote.service.ReportsService
import com.dailyapps.data.utils.apiCall
import com.dailyapps.data.utils.mapFromApiResponse
import com.dailyapps.domain.usecase.GetReportByIdUseCase
import com.dailyapps.domain.usecase.GetReportsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.PostReportUseCase
import com.dailyapps.domain.usecase.UpdateReportUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Report
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri
import com.dailyapps.data.mapper.SingleReportMapper
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class ReportsRemoteDataSource @Inject constructor(
    private val reportsService: ReportsService,
    private val reportMapper: ReportMapper,
    private val singleReportMapper: SingleReportMapper,
    private val downloadReportMapper: DownloadReportMapper,
    @ApplicationContext private val context: Context
) {
    suspend fun getReportsByUserIdByDate(params: GetReportsByUserIdByDateUseCase.Params): Flow<Resource<List<Report>>> {
        return mapFromApiResponse(
            result = apiCall {
                reportsService.getReportsByUserIdByDate(
                    params.token.formatToken(),
                    params.userId,
                    params.startDate,
                    params.endDate,
                    params.lecturer
                )
            },
            reportMapper
        )
    }

    suspend fun downloadReport(id: Long, token: String): Flow<Resource<String>> {
        return mapFromApiResponse(
            result = apiCall {
                reportsService.downloadReport(token.formatToken(), id)
            },
            downloadReportMapper
        )
    }

    suspend fun postReport(params: PostReportUseCase.Params): Flow<Resource<Report>> {
        val uri = params.documentUri.toUri()
        val file = getFileFromUri(uri)

        // Create request body parts
        val userIdPart = params.userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lecturerIdPart =
            params.lecturerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val datePart = params.date.toRequestBody("text/plain".toMediaTypeOrNull())

        // Create file part
        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData(
            "document",
            file.name,
            requestFile
        )

        return mapFromApiResponse(
            result = apiCall {
                reportsService.submitReport(
                    params.token.formatToken(),
                    userIdPart,
                    lecturerIdPart,
                    datePart,
                    filePart
                )
            },
            singleReportMapper
        )
    }

    suspend fun getReportById(params: GetReportByIdUseCase.Params): Flow<Resource<Report>> {
        return mapFromApiResponse(
            result = apiCall {
                reportsService.getReportById(
                    params.token.formatToken(),
                    params.id
                )
            },
            singleReportMapper
        )
    }

    suspend fun updateReport(params: UpdateReportUseCase.Params): Flow<Resource<Report>> {
        // Handle both remote URLs and local file URIs
        val uri = if (params.documentUri.startsWith("http")) {
            null // It's a remote URL, we won't upload a new file
        } else {
            params.documentUri.toUri()
        }

        // Create request body parts
        val userIdPart = params.userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lecturerIdPart = params.lecturerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val datePart = params.date.toRequestBody("text/plain".toMediaTypeOrNull())

        // Create file part if we have a local URI
        val filePart = if (uri != null) {
            val file = getFileFromUri(uri)
            val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("document", file.name, requestFile)
        } else {
            // Create an empty part or a part with the URL if no file is being uploaded
            // This assumes the backend can handle an empty file part when no new file is being uploaded
            val emptyBytes = ByteArray(0)
            val requestBody = emptyBytes.toRequestBody("application/octet-stream".toMediaTypeOrNull(), 0, 0)
            MultipartBody.Part.createFormData("document", "", requestBody)
        }

        return mapFromApiResponse(
            result = apiCall {
                reportsService.updateReport(
                    params.token.formatToken(),
                    params.id,
                    userIdPart,
                    lecturerIdPart,
                    datePart,
                    filePart
                )
            },
            singleReportMapper
        )
    }

    // Helper method implementations
    private fun getFileFromUri(uri: Uri): java.io.File {
        val contentResolver = context.contentResolver
        val fileName = getFileNameFromUri(uri) ?: "document_${System.currentTimeMillis()}"
        val tempFile = java.io.File(context.cacheDir, fileName)

        tempFile.outputStream().use { outputStream ->
            contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return tempFile
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return null

        return cursor.use { c ->
            if (c.moveToFirst()) {
                val nameIndex = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) c.getString(nameIndex) else null
            } else null
        }
    }
}
