package com.dailyapps.data.utils

import com.dailyapps.domain.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun<ResultType> apiCall(skipState: Boolean = false, api: suspend() -> ResultType): Flow<Resource<ResultType>> {
    return withContext(Dispatchers.IO) {
        flow {
            if (!skipState) emit(Resource.Loading)
            try {
                emit(Resource.Success(api.invoke()))
            }
            catch (e: Throwable) {
                emit(Resource.Error(msg = message(e)))
            }
        }.catch { error ->
            error.printStackTrace()
            Timber.e(error.localizedMessage)
            emit(Resource.Loading)
            delay(5)
            emit(Resource.Error(msg = message(error)))
        }
    }
}

inline fun message(throwable: Throwable?):String{
    return when (throwable) {
        is SocketTimeoutException -> return "Whoops! Connection time out. Please try again"
        is IOException -> return "Whoops! No Internet Connection. Please try again"
        is HttpException -> return try {
            val errorJsonString = throwable.response()?.errorBody()?.string()
            val errorMessage = JSONObject(errorJsonString).getJSONObject("meta")
            Timber.e(">>> ${errorMessage.getString("message")}")
            errorMessage.getString("message")
        }catch (e:Exception){
            "Whoops! Unknown error occurred. Please try again"
        }

        else -> {"Whoops!: ${throwable?.message}"}
    }
}
inline fun code(throwable: Throwable?):Int{
    return if (throwable is HttpException) (throwable).code()
    else  0
}
