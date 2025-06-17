package com.dailyapps.data.utils

import com.dailyapps.apirequest.SubmitExamRequestItem
import com.dailyapps.apiresponse.AbsentDataItemApiResponse
import com.dailyapps.apiresponse.BaseResponse
import com.dailyapps.apiresponse.ClassRoomApiResponse
import com.dailyapps.apiresponse.ExamQuestionItemApiResponse
import com.dailyapps.apiresponse.GuruApiResponse
import com.dailyapps.apiresponse.JenisNilaiApiResponse
import com.dailyapps.apiresponse.LinkItemApiResponse
import com.dailyapps.apiresponse.MatpelApiResponse
import com.dailyapps.apiresponse.NamaNilaiApiResponse
import com.dailyapps.apiresponse.SchoolYearApiResponse
import com.dailyapps.apiresponse.SiswaApiResponse
import com.dailyapps.apiresponse.StudentApiResponse
import com.dailyapps.apiresponse.TeachersApiResponse
import com.dailyapps.data.local.room.entity.ClassRoomEntity
import com.dailyapps.data.local.room.entity.SchoolYearEntity
import com.dailyapps.data.local.room.entity.StudentEntity
import com.dailyapps.data.local.room.entity.TeacherEntity
import com.dailyapps.data.local.room.entity.UserEntity
import com.dailyapps.entity.AbsentDataItem
import com.dailyapps.entity.ClassRoom
import com.dailyapps.entity.ExamItem
import com.dailyapps.entity.Guru
import com.dailyapps.entity.JenisNilai
import com.dailyapps.entity.LinkItem
import com.dailyapps.entity.Matpel
import com.dailyapps.entity.NamaNilai
import com.dailyapps.entity.SchoolYear
import com.dailyapps.entity.Siswa
import com.dailyapps.entity.Student
import com.dailyapps.entity.SubmitExamRequest
import com.dailyapps.entity.Teacher
import com.dailyapps.entity.User

object DataMapper {

    //region Absent Mapper

    fun List<AbsentDataItemApiResponse>.toAbsentDataItems() = map { it.toAbsentDataItem() }

    fun AbsentDataItemApiResponse.toAbsentDataItem() = AbsentDataItem(
        jenisAbsensiId,
        tanggalAbsensi,
        studentApiResponse?.toStudent(),
        latitude, createdAt, jenisAbsensi, foto, updatedAt, jamMasuk, tahunAjaranSemesterId, id, siswaId, longitude
    )

    private fun StudentApiResponse.toStudent() = Student(
        nama, noHp, foto, updatedAt, nisn, createdAt, id, jenisKelamin, kelasId, deletedAt, alamat
    )

    fun List<LinkItemApiResponse>.toLinkItems() = map { it.toLinkItem() }

    private fun LinkItemApiResponse.toLinkItem() = LinkItem(active, label, url)

    //endregion
    // region School Year Mapper
    fun BaseResponse<List<SchoolYearApiResponse>>.toDomainSchoolYears() =
        data.map {
            it.toDomainSchoolYear()
        }

    fun SchoolYearApiResponse.toDomainSchoolYear() = SchoolYear(updatedAt, createdAt, semester, id, tahunAjaran, deletedAt)

    fun List<SchoolYear>.toEntitySchoolYears() = map {
        it.toEntitySchoolYear()
    }

    private fun SchoolYear.toEntitySchoolYear() = SchoolYearEntity(
        id = id, semester = semester, tahunAjaran = tahunAjaran, deletedAt = deletedAt, createdAt = createdAt, updatedAt = updatedAt)

    fun List<SchoolYearEntity>.toDomainSchoolYears() = map {
        it.toDomainSchoolYear()
    }

    fun SchoolYearEntity.toDomainSchoolYear() =
        SchoolYear(updatedAt, createdAt, semester, id, tahunAjaran, deletedAt)


    //endregion
    //region User Mapper

    fun User.toUserEntity() =
        UserEntity(
            role = role,
            address = address,
            nim = nim,
            name = name,
            stase = stase,
            endSchoolYear = endSchoolYear,
            id = id,
            email = email,
            startSchoolYear = startSchoolYear,
            token = token
        )

    fun UserEntity.toUser() = User(
        role = role,
        address = address,
        nim = nim,
        name = name,
        stase = stase,
        endSchoolYear = endSchoolYear,
        id = id,
        email = email,
        startSchoolYear = startSchoolYear,
        token = token
    )

    //endregion
    //region Class Room Mapper

    fun BaseResponse<List<ClassRoomApiResponse>>.toDomainClassRooms() =
        data.map {
            it.toDomainClassRoom()
        }

    fun ClassRoomApiResponse.toDomainClassRoom() = ClassRoom(updatedAt, kelas, createdAt, id, deletedAt)

    fun List<ClassRoom>.toEntityClassRooms() = map {
        it.toEntityClassRoom()
    }

    private fun ClassRoom.toEntityClassRoom() = ClassRoomEntity(updatedAt, kelas, createdAt, id, deletedAt)

    fun List<ClassRoomEntity>.toDomainClassRooms() = map {
        it.toDomainClassRoom()
    }
    fun ClassRoomEntity.toDomainClassRoom() = ClassRoom(updatedAt, kelas, createdAt, id, deletedAt)

    //endregion
    //region Teacher Mapper

    fun BaseResponse<List<TeachersApiResponse>>.toDomainTeachers() =
        data.map {
            it.toDomainTeacher()
        }

    private fun TeachersApiResponse.toDomainTeacher() = Teacher(name, email, id , role)

    fun List<Teacher>.toEntityTeachers() = map {
        it.toEntityTeacher()
    }

    private fun Teacher.toEntityTeacher() = TeacherEntity(name, email, id, role)

    //endregion
    //region Score

    fun JenisNilaiApiResponse.toJenisNilaiDomain() =
        JenisNilai(jenisNilai, id)

    fun SiswaApiResponse.toSiswaDomain() =
        Siswa(nama, noHp, jenisKelamin, updatedAt, nisn, createdAt, id, jenisKelamin, kelasId, deletedAt, alamat)

    fun GuruApiResponse.toGuruDomain() = Guru(
        nama, noHp, foto, updatedAt, createdAt, id, noGuru, jenisKelamin, deletedAt, alamat
    )

    fun NamaNilaiApiResponse.toNamaNilaiDomain() = NamaNilai(
        namaNilai, id
    )

    fun MatpelApiResponse.toMapelDomain() = Matpel(
       updatedAt, createdAt, matpel, id, deletedAt
    )

    //endregion
    //student

    fun BaseResponse<List<StudentApiResponse>>.toDomainStudents() =
        data.map {
            it.toDomainStudent()
        }

    private fun StudentApiResponse.toDomainStudent() = Student(
        nama, noHp, foto, updatedAt, nisn, createdAt, id, jenisKelamin, kelasId, deletedAt, alamat, kelas = kelas?.toDomainClassRoom()
    )

    fun List<Student>.toEntityStudents() = map {
        it.toEntityStudent()
    }

    private fun Student.toEntityStudent() = StudentEntity(
        id, nama, noHp, foto, updatedAt, nisn, createdAt, jenisKelamin, kelasId, kelas = kelas?.kelas
    )

    fun StudentEntity.toDomainStudent() = Student(
        nama, noHp, foto, updatedAt, nisn, createdAt, id, jenisKelamin, kelasId, deletedAt, alamat, namaKelas = kelas
    )

    //endregion

    // ExamQuestion
    fun ExamQuestionItemApiResponse.toDomainExamItem() = ExamItem(
        namaNilaiId, isStart, ujianStart, isActive, jenisUjian, createdAt, matpelId, guruId, deletedAt, token, ujianEnd, expiredDuration, updatedAt, tahunAjaranSemesterId, isFinish, id
    )
    // End Region

    // SubmitExam
    fun SubmitExamRequest.toDomainSubmitExamRequest(): SubmitExamRequestItem =
        SubmitExamRequestItem(
            soalId, siswaId, jawaban
        )
    // endregion
}