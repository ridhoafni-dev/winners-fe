package com.dailyapps.domain.usecase

import com.dailyapps.entity.User
import kotlinx.coroutines.flow.Flow

interface UserUseCase {
    fun getUser(): Flow<User>
    suspend fun storeUser(user: User): Flow<Boolean>
    suspend fun logout()
    suspend fun storeToken(token: String)
    suspend fun storeTokenType(tokenType: String)
    suspend fun storeUsername(username: String)
    suspend fun storeId(id: Int)
    suspend fun getIsLoggedIn(): Flow<Boolean>
    suspend fun getCurrentUsername(): Flow<String>
    suspend fun getCurrentId(): Flow<Int>
    suspend fun getCurrentToken(): Flow<String>
    suspend fun getCurrentTokenType(): Flow<String>
}