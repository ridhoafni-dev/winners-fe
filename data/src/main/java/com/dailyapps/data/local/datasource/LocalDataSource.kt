package com.dailyapps.data.local.datasource

import com.dailyapps.entity.User
import com.dailyapps.data.local.datastore.MainDataStore
import com.dailyapps.data.local.room.dao.ClassRoomDao
import com.dailyapps.data.local.room.dao.SchoolYearDao
import com.dailyapps.data.local.room.dao.StudentDao
import com.dailyapps.data.local.room.dao.TeacherDao
import com.dailyapps.data.local.room.dao.UserDao
import com.dailyapps.data.utils.DataMapper.toUser
import com.dailyapps.data.utils.DataMapper.toUserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val userDao: UserDao,
    private val schoolYearDao: SchoolYearDao,
    private val studentDao: StudentDao,
    private val teacherDao: TeacherDao,
    private val classDao: ClassRoomDao,
    private val mainDataStore: MainDataStore
) {

    fun isLoggedIn() = flow {
        mainDataStore.token.collect { emit(it.isNotEmpty()) }
    }.catch {
        Timber.e("LocalDataSource", "isLoggedIn: failed=${it.message}")
    }.flowOn(Dispatchers.IO)

    suspend fun logout() = mainDataStore.clear()

    suspend fun clearTable() {
        coroutineScope {
            userDao.deleteAllUser()
            schoolYearDao.deleteAllSchoolYear()
            studentDao.deleteAllStudent()
            teacherDao.deleteAllTeacher()
            classDao.deleteAllClass()
        }
    }


    fun getCurrentUsername() = flow {
        mainDataStore.username.collect{ emit(it) }
    }.catch {
        Timber.e("LocalDataSource", "getCurrentUsername: failed=${it.message}")
    }.flowOn(Dispatchers.IO)

    fun getCurrentToken() = flow {
        mainDataStore.token.collect{ emit(it) }
    }.catch {
        Timber.e("LocalDataSource", "getCurrentToken: failed=${it.message}")
    }.flowOn(Dispatchers.IO)

    fun getCurrentTokenType() = flow {
        mainDataStore.tokenType.collect{ emit(it) }
    }.catch {
        Timber.e("LocalDataSource", "getCurrentTokenType: failed=${it.message}")
    }.flowOn(Dispatchers.IO)

    fun getCurrentId() = flow {
        mainDataStore.id.collect { emit(it) }
    }.catch {
        Timber.e("LocalDataSource", "getCurrentId: failed=${it.message}")
    }.flowOn(Dispatchers.IO)

    suspend fun storeUsername(username: String) = mainDataStore.storeData(MainDataStore.USERNAME, username)
    suspend fun storeTokenType(tokenType: String) = mainDataStore.storeData(MainDataStore.TOKEN_TYPE, tokenType)
    suspend fun storeToken(token: String) = mainDataStore.storeData(MainDataStore.TOKEN, token)
    suspend fun storeId(id: Int) = mainDataStore.storeData(MainDataStore.ID, id)

    fun storeUser(user: User) = flow {
        userDao.storeUser(user.toUserEntity())
        emit(true)
    }.catch {
        Timber.e("LocalDataSource", "storeUser: failed=${it.message}")
        emit(false)
    }.flowOn(Dispatchers.IO)

    fun getUser() = flow {
        userDao.getUserByUsernameNisn().collect {
            emit(it.toUser())
        }
    }.catch {
        Timber.e("LocalDataSource", "getUser: failed=${it.message}")
    }.flowOn(Dispatchers.IO)

}