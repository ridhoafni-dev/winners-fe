package com.dailyapps.data.repository

import com.dailyapps.data.local.datasource.LocalDataSource
import com.dailyapps.domain.repository.IUserRepository
import com.dailyapps.entity.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val localDataSource: LocalDataSource
): IUserRepository {

    override fun getUser(): Flow<User> = localDataSource.getUser()

    override suspend fun storeUser(user: User): Flow<Boolean> = localDataSource.storeUser(user)

    override suspend fun storeId(id: Int) = localDataSource.storeId(id)

    override suspend fun storeToken(token: String) = localDataSource.storeToken(token)
    override suspend fun storeTokenType(tokenType: String) = localDataSource.storeTokenType(tokenType)

    override suspend fun storeUsername(username: String) = localDataSource.storeUsername(username)

    override suspend fun logout() {
        localDataSource.logout()
        localDataSource.clearTable()
    }

    override suspend fun getIsLoggedIn(): Flow<Boolean> = localDataSource.isLoggedIn()

    override suspend fun getCurrentUsername(): Flow<String> = localDataSource.getCurrentUsername()

    override suspend fun getCurrentId(): Flow<Int> = localDataSource.getCurrentId()

    override suspend fun getCurrentToken(): Flow<String> = localDataSource.getCurrentToken()
    override suspend fun getCurrentTokenType(): Flow<String> = localDataSource.getCurrentTokenType()

}