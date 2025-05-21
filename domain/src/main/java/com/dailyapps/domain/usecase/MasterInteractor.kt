package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IMasterRepository
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ClassRoom
import com.dailyapps.entity.SchoolYear
import com.dailyapps.entity.Student
import com.dailyapps.entity.Teacher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MasterInteractor @Inject constructor(
    private val masterRepository: IMasterRepository
): MasterUseCase {
    override suspend fun getAllSchoolYears(): Flow<Resource<List<SchoolYear>>> =
        masterRepository.getAllSchoolYears()

    override suspend fun getAllTeachers(): Flow<Resource<List<Teacher>>> =
        masterRepository.getAllTeachers()


    override suspend fun getRemoteClassRooms(): Flow<Resource<List<ClassRoom>>> =
        masterRepository.getRemoteClassRooms()

    override suspend fun getAllClassRooms(): Flow<Resource.Success<List<ClassRoom>>> =
        masterRepository.getAllClassRoom()

    override fun getMasterSchoolYears(): Flow<Resource.Success<List<SchoolYear>>> = masterRepository.getMasterSchoolYears()
    override suspend fun getSchoolYearId(year: String, sem: String): Resource.Success<SchoolYear> =
        masterRepository.getSchoolYearId(year, sem)

    override suspend fun getClassRoomId(classRoom: String): Resource.Success<ClassRoom> =
        masterRepository.getClassRoomId(classRoom)
    override suspend fun getStudentClass(): Resource.Success<Student> =
        masterRepository.getStudentClass()
}