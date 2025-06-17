package com.dailyapps.data.remote.datasource

import com.dailyapps.data.local.datasource.MasterLocalDataSource
import com.dailyapps.data.remote.SafeApiCall
import com.dailyapps.data.remote.service.MasterService
import com.dailyapps.data.utils.DataMapper.toDomainClassRooms
import com.dailyapps.data.utils.DataMapper.toDomainSchoolYears
import com.dailyapps.data.utils.DataMapper.toDomainTeachers
import com.dailyapps.data.utils.DataMapper.toEntityClassRooms
import com.dailyapps.data.utils.DataMapper.toEntitySchoolYears
import com.dailyapps.data.utils.DataMapper.toEntityTeachers
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ClassRoom
import com.dailyapps.entity.SchoolYear
import com.dailyapps.entity.Teacher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterRemoteDataSource @Inject constructor(
    private val masterService: MasterService,
    private val masterLocalDataSource: MasterLocalDataSource
): SafeApiCall {

    //region School Year

    suspend fun getAllSchoolYears(): Flow<Resource<List<SchoolYear>>> = flow {
        runCatching {
            val apiResult = safeApiCall { masterService.getAllStudentYear() }
            val apiResource: Resource<List<SchoolYear>> = when(apiResult) {
                is Resource.Error -> Resource.Error(apiResult.msg)
                is Resource.Success -> with(apiResult.data) {
                    toDomainSchoolYears().run {
                        masterLocalDataSource.replaceAllSchoolYears(toEntitySchoolYears())
                        Resource.Success(this)
                    }
                }
                is Resource.Loading -> Resource.Loading
            }
            emit(apiResource)
        }
        .onFailure {
            Resource.Error("Unknow error")
        }

    }

    //endregion
    //region Teacher

    fun getAllTeachers(): Flow<Resource<List<Teacher>>> = flow {
        runCatching {
            val apiResult = safeApiCall { masterService.getAllTeacher() }
            val apiResource: Resource<List<Teacher>> = when(apiResult) {
                is Resource.Error -> Resource.Error(apiResult.msg)
                is Resource.Success -> with(apiResult.data) {
                    toDomainTeachers().run {
                        masterLocalDataSource.replaceAllTeacher(toEntityTeachers())
                        Resource.Success(this)
                    }
                }
                is Resource.Loading -> Resource.Loading
            }
            emit(apiResource)
        }
        .onFailure {
            Resource.Error("Unknow error")
        }

    }

    //endregion
    //region Class Room

    suspend fun getAllClassRoom(): Flow<Resource<List<ClassRoom>>> = flow {
        runCatching {
            val apiResult = safeApiCall { masterService.getAllClassRoom() }
            val apiResource: Resource<List<ClassRoom>> = when(apiResult) {
                is Resource.Error -> Resource.Error(apiResult.msg)
                is Resource.Success -> with(apiResult.data) {
                    toDomainClassRooms().run {
                        masterLocalDataSource.replaceAllClassRoom(toEntityClassRooms())
                        Resource.Success(this)
                    }
                }
                is Resource.Loading -> Resource.Loading
            }
            emit(apiResource)
        }
        .onFailure {
            Resource.Error("Unknow error")
        }

    }

    //endregion
}