package com.dailyapps.winners.di

import com.dailyapps.data.module.ApiServiceModule
import com.dailyapps.data.module.DatabaseModule
import com.dailyapps.data.repository.AbsentRepository
import com.dailyapps.data.repository.AuthRepository
import com.dailyapps.data.repository.ExamQuestionRepository
import com.dailyapps.data.repository.ExamRepository
import com.dailyapps.data.repository.MasterRepository
import com.dailyapps.data.repository.NoteRepository
import com.dailyapps.data.repository.ObservationRepository
import com.dailyapps.data.repository.ScoreRepository
import com.dailyapps.data.repository.StudentRepository
import com.dailyapps.data.repository.UserRepository
import com.dailyapps.domain.repository.IAbsentRepository
import com.dailyapps.domain.repository.IAuthRepository
import com.dailyapps.domain.repository.IExamQuestionRepository
import com.dailyapps.domain.repository.IExamRepository
import com.dailyapps.domain.repository.IMasterRepository
import com.dailyapps.domain.repository.INoteRepository
import com.dailyapps.domain.repository.IObservationRepository
import com.dailyapps.domain.repository.IScoreRepository
import com.dailyapps.domain.repository.IStudentRepository
import com.dailyapps.domain.repository.IUserRepository
import com.dailyapps.domain.usecase.MasterInteractor
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.UserInteractor
import com.dailyapps.domain.usecase.UserUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [ApiServiceModule::class, DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepoImpl: UserRepository): IUserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepoImpl: AuthRepository): IAuthRepository

    @Binds
    @Singleton
    abstract fun provideUserUseCase(userInteractor: UserInteractor): UserUseCase

    @Binds
    @Singleton
    abstract fun provideAbsentRepository(absentRepository: AbsentRepository): IAbsentRepository

    @Binds
    @Singleton
    abstract fun provideNoteRepository(noteRepository: NoteRepository): INoteRepository

    @Binds
    @Singleton
    abstract fun bindMasterRepository(masterRepository: MasterRepository): IMasterRepository

    @Binds
    @Singleton
    abstract fun provideMasterUseCase(masterInteractor: MasterInteractor): MasterUseCase

    @Binds
    @Singleton
    abstract fun provideScoreRepository(scoreRepository: ScoreRepository): IScoreRepository

    @Binds
    @Singleton
    abstract fun provideStudentRepository(scoreRepository: StudentRepository): IStudentRepository

    @Binds
    @Singleton
    abstract fun provideExamRepository(examRepository: ExamRepository): IExamRepository

    @Binds
    @Singleton
    abstract fun provideExamQuestionRepository(examQuestionRepository: ExamQuestionRepository): IExamQuestionRepository

    @Binds
    @Singleton
    abstract fun provideObservationRepository(observationRepository: ObservationRepository): IObservationRepository

//    @Binds
//    @Singleton
//    abstract fun provideObservationUseCase(observationInteractor: ObservationI): ObservationUseCase): MasterUseCase

}