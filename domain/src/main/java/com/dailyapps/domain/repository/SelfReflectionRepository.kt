package com.dailyapps.domain.repository

import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.SelfReflection
import kotlinx.coroutines.flow.Flow

interface SelfReflectionRepository {
    fun getSelfReflectionsByUserIdByDate(
        token: String,
        userId: Long,
        startDate: String,
        endDate: String,
        lecturer: Boolean = false
    ): Flow<Resource<List<SelfReflection>>>

    fun getSelfReflectionById(
        token: String,
        id: Long
    ): Flow<Resource<SelfReflection>>

    fun addSelfReflection(
        token: String,
        title: String,
        userId: Long,
        lecturerId: Long
    ): Flow<Resource<SelfReflection>>

    fun updateSelfReflection(
        token: String,
        id: Long,
        title: String,
        userId: Long,
        lecturerId: Long
    ): Flow<Resource<SelfReflection>>

    fun addSelfReflectionComment(
        token: String,
        selfReflectionId: Long,
        userId: Long,
        comment: String,
        rating: Int
    ): Flow<Resource<SelfReflection>>
}
