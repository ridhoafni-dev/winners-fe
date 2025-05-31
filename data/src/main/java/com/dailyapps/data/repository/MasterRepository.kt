package com.dailyapps.data.repository

import com.dailyapps.data.local.datasource.MasterLocalDataSource
import com.dailyapps.data.remote.datasource.MasterRemoteDataSource
import com.dailyapps.data.utils.DataMapper.toDomainStudent
import com.dailyapps.domain.repository.IMasterRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ClassRoom
import com.dailyapps.entity.SchoolYear
import com.dailyapps.entity.Student
import com.dailyapps.entity.Teacher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterRepository @Inject constructor(
    private val masterRemoteDataSource: MasterRemoteDataSource,
    private val masterLocalDataSource: MasterLocalDataSource
): IMasterRepository {

    //region School Year

    override suspend fun getAllSchoolYears(): Flow<Resource<List<SchoolYear>>> =
        masterRemoteDataSource.getAllSchoolYears()

    override suspend fun getMasterSchoolYears(): Flow<Resource.Success<List<SchoolYear>>> =
        masterLocalDataSource.getMasterSchoolYears()

    override suspend fun getSchoolYearId(year: String, sem: String): Resource.Success<SchoolYear> =
        masterLocalDataSource.getSchoolYearId(year, sem)


    //endregion
    //region Teacher

    override suspend fun getAllTeachers(): Flow<Resource<List<Teacher>>> =
        masterRemoteDataSource.getAllTeachers()

    override suspend fun getAllTeachersAsFlow(): Flow<List<Teacher>> = masterLocalDataSource.getAllTeachersAsFlow()

    //endregion
    //region Class Room

    override suspend fun getRemoteClassRooms(): Flow<Resource<List<ClassRoom>>> =
        masterRemoteDataSource.getAllClassRoom()

    override suspend fun getAllClassRoom(): Flow<Resource.Success<List<ClassRoom>>> =
        masterLocalDataSource.getAllClassRoom()
    override suspend fun getClassRoomId(classRoom: String): Resource.Success<ClassRoom> =
        masterLocalDataSource.getClassRoomId(classRoom)

    //endregion
    //student

    override suspend fun getStudentClass(): Resource.Success<Student> =
        Resource.Success(masterLocalDataSource.getStudentClass().toDomainStudent())


    //end

}