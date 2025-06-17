package com.dailyapps.data.module

import com.dailyapps.data.remote.service.AbsentService
import com.dailyapps.data.remote.service.ActivityPlanService
import com.dailyapps.data.remote.service.AuthService
import com.dailyapps.data.remote.service.ExamQuestionService
import com.dailyapps.data.remote.service.ExamService
import com.dailyapps.data.remote.service.MasterService
import com.dailyapps.data.remote.service.NoteService
import com.dailyapps.data.remote.service.ObservationService
import com.dailyapps.data.remote.service.ReportsService
import com.dailyapps.data.remote.service.ScoreService
import com.dailyapps.data.remote.service.StudentService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiServiceModule {

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
    @Provides
    @Singleton
    fun provideAbsentService(retrofit: Retrofit): AbsentService {
        return retrofit.create(AbsentService::class.java)
    }

    @Provides
    @Singleton
    fun provideNoteService(retrofit: Retrofit): NoteService {
        return retrofit.create(NoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideMasterService(retrofit: Retrofit): MasterService {
        return retrofit.create(MasterService::class.java)
    }
    @Provides
    @Singleton
    fun provideScoreService(retrofit: Retrofit): ScoreService {
        return retrofit.create(ScoreService::class.java)
    }
    @Provides
    @Singleton
    fun provideStudentService(retrofit: Retrofit): StudentService {
        return retrofit.create(StudentService::class.java)
    }

    @Provides
    @Singleton
    fun provideExamService(retrofit: Retrofit): ExamService {
        return retrofit.create(ExamService::class.java)
    }

    @Provides
    @Singleton
    fun provideExamQuestionService(retrofit: Retrofit): ExamQuestionService {
        return retrofit.create(ExamQuestionService::class.java)
    }

    @Provides
    @Singleton
    fun provideObservationService(retrofit: Retrofit): ObservationService {
        return retrofit.create(ObservationService::class.java)
    }

    @Provides
    @Singleton
    fun provideActivityPlanService(retrofit: Retrofit): ActivityPlanService {
        return retrofit.create(ActivityPlanService::class.java)
    }

    @Provides
    @Singleton
    fun provideReportsService(retrofit: Retrofit): ReportsService {
        return retrofit.create(ReportsService::class.java)
    }

}