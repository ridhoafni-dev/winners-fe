package com.dailyapps.domain.repository

import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.ClassRoom
import com.dailyapps.entity.SchoolYear
import com.dailyapps.entity.Student
import com.dailyapps.entity.Teacher
import kotlinx.coroutines.flow.Flow

interface IMasterRepository {
    suspend fun getAllSchoolYears(): Flow<Resource<List<SchoolYear>>>
    suspend fun getAllTeachers(): Flow<Resource<List<Teacher>>>
    suspend fun getAllTeachersAsFlow(): Flow<List<Teacher>>
    suspend fun getRemoteClassRooms(): Flow<Resource<List<ClassRoom>>>
    suspend fun getAllClassRoom(): Flow<Resource.Success<List<ClassRoom>>>
    suspend fun getMasterSchoolYears(): Flow<Resource.Success<List<SchoolYear>>>
    suspend fun getSchoolYearId(year: String, sem: String): Resource.Success<SchoolYear>
    suspend fun getClassRoomId(classRoom: String): Resource.Success<ClassRoom>
    suspend fun getStudentClass(): Resource.Success<Student>
}