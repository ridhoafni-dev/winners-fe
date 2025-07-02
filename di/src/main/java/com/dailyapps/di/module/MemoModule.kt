package com.dailyapps.di.module

import com.dailyapps.data.remote.service.MemoService
import com.dailyapps.data.repository.MemoRepository
import com.dailyapps.domain.repository.IMemoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MemoModule {

    @Binds
    @Singleton
    abstract fun provideMemoRepository(repository: MemoRepository): IMemoRepository

    companion object {
        @Provides
        @Singleton
        fun provideMemoService(retrofit: Retrofit): MemoService {
            return retrofit.create(MemoService::class.java)
        }
    }
}
