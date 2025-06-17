package com.dailyapps.domain.usecase

import com.dailyapps.domain.repository.IUserRepository
import com.dailyapps.entity.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserInteractor @Inject constructor(
     private val userRepository: IUserRepository
): UserUseCase {
    override fun getUser(): Flow<User> = userRepository.getUser()

    override suspend fun storeUser(user: User): Flow<Boolean> = userRepository.storeUser(user)

    override suspend fun logout() = userRepository.logout()

    override suspend fun storeToken(token: String) = userRepository.storeToken(token)
    override suspend fun storeTokenType(tokenType: String) = userRepository.storeTokenType(tokenType)

    override suspend fun storeUsername(username: String) = userRepository.storeUsername(username)

    override suspend fun storeId(id: Int) = userRepository.storeId(id)

    override suspend fun getIsLoggedIn(): Flow<Boolean> = userRepository.getIsLoggedIn()

    override suspend fun getCurrentUsername(): Flow<String> = userRepository.getCurrentUsername()

    override suspend fun getCurrentId(): Flow<Int> = userRepository.getCurrentId()

    override suspend fun getCurrentToken(): Flow<String> = userRepository.getCurrentToken()
    override suspend fun getCurrentTokenType(): Flow<String>  = userRepository.getCurrentTokenType()
}