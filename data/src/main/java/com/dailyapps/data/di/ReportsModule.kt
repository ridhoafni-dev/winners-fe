package com.dailyapps.data.di

import com.dailyapps.data.remote.datasource.ReportsRemoteDataSource
import com.dailyapps.data.repository.ReportsRepository
import com.dailyapps.domain.repository.IReportsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportsModule {

    @Provides
    @Singleton
    fun provideReportsRepository(remoteDataSource: ReportsRemoteDataSource): IReportsRepository {
        return ReportsRepository(remoteDataSource)
    }
}