package com.dailyapps.data.remote.datasource

import android.content.Context
import android.net.Uri
import com.dailyapps.common.utils.formatToken
import com.dailyapps.data.mapper.AddObservationMapper
import com.dailyapps.data.mapper.ObservationDetailMapper
import com.dailyapps.data.mapper.ObservationMapper
import com.dailyapps.data.remote.service.ObservationService
import com.dailyapps.data.utils.apiCall
import com.dailyapps.data.utils.mapFromApiResponse
import com.dailyapps.domain.usecase.AddObservationUseCase
import com.dailyapps.domain.usecase.GetObservationByIdUseCase
import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.Observation
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
import com.dailyapps.domain.usecase.UpdateObservationUseCase
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class ObservationRemoteDataSource @Inject constructor(
    private val observationService: ObservationService,
    private val observationMapper: ObservationMapper,
    private val observationDetailMapper: ObservationDetailMapper,
    private val addObservationMapper: AddObservationMapper,
    @ApplicationContext private val context: Context
){
    suspend fun getObservationsByUserIdByDate(params: GetObservationsByUserIdByDateUseCase.Params) : Flow<Resource<List<Observation>>> {
        return mapFromApiResponse(
            result = apiCall {
                observationService.getObservationsByUserIdByDate(params.token.formatToken(), params.userId, params.startDate, params.endDate, params.lecturer)
            }, observationMapper
        )
    }
    suspend fun getObservationById(params: GetObservationByIdUseCase.Params) : Flow<Resource<Observation>> {
        return mapFromApiResponse(
            result = apiCall {
                observationService.getObservationById(params.token.formatToken(), params.id)
            }, observationDetailMapper
        )
    }

    suspend fun addObservation(params: AddObservationUseCase.Params): Flow<Resource<Observation>> {
        return mapFromApiResponse(
            result = apiCall {
                observationService.addObservation(
                    token = params.token.formatToken(),
                    userId = params.userId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    name = params.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    description = params.description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    date = params.date.toRequestBody("text/plain".toMediaTypeOrNull()),
                    lecturerId = params.lecturerId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = params.imageUri?.let {
                        val file = uploadImage(it, context)
                        file?.let {
                            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("image", it.name, requestFile)
                        }
                    }                )
            },
            addObservationMapper
        )
    }


    suspend fun updateObservation(params: UpdateObservationUseCase.Params): Flow<Resource<Observation>> {
        return mapFromApiResponse(
            result = apiCall {
                observationService.updateObservation(
                    id = params.id,
                    token = params.token.formatToken(),
                    userId = params.userId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    name = params.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    description = params.description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    date = params.date.toRequestBody("text/plain".toMediaTypeOrNull()),
                    lecturerId = params.lecturerId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    active = "true".toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = params.imageUri?.let {
                        val file = uploadImage(it, context)
                        file?.let {
                            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("image", it.name, requestFile)
                        }
                    }                )
            },
            addObservationMapper
        )
    }

    // Add to your repository implementation
    private fun uploadImage(imageUriString: String?, context: Context): File? {
        if (imageUriString == null) return null

        try {
            val uri = imageUriString.toUri()
            return getFileFromContentUri(context, uri)
        } catch (e: Exception) {
            Timber.e(e, "Error processing image URI")
            return null
        }
    }

    private fun getFileFromContentUri(context: Context, contentUri: Uri): File {
        // Create a file in the cache directory
        val fileName = "image_${System.currentTimeMillis()}"
        val fileExtension = getFileExtension(context, contentUri)
        val cacheFile = File(context.cacheDir, "$fileName$fileExtension")

        // Copy the content URI stream to the cache file
        context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
            cacheFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return cacheFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String {
        val mime = context.contentResolver.getType(uri)
        return when {
            mime?.contains("jpeg") == true -> ".jpg"
            mime?.contains("jpg") == true -> ".jpg"
            mime?.contains("png") == true -> ".png"
            else -> ".jpg" // Default extension
        }
    }
}
