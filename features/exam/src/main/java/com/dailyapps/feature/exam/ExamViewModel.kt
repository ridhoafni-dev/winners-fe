package com.dailyapps.feature.exam

import androidx.lifecycle.viewModelScope
import com.dailyapps.domain.usecase.ExamListUseCase
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.entity.SchoolYear
import com.dailyapps.entity.Student
import com.dailyapps.feature.exam.pages.detail.ExamScreenType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val examUseCase: ExamListUseCase,
    private val masterUseCase: MasterUseCase,
    private val userUseCase: UserUseCase
) : BaseViewModel() {

    private val _examScreenState = MutableStateFlow<ExamScreenState>(ExamScreenState.Loading)
    internal val examScreenState get() = _examScreenState

    private val _examDetailScreenState = MutableStateFlow<ExamScreenState>(ExamScreenState.Loading)
    internal val examDetailScreenState get() = _examDetailScreenState

    private val _masterSchoolYears = MutableStateFlow<List<SchoolYear>>(emptyList())
    internal val masterSchoolYear get() = _masterSchoolYears

    private val _currentToken = MutableStateFlow("")
    private val currentToken get() = _currentToken

    private val _currentTokenType = MutableStateFlow("")
    private val currentTokenType get() = _currentTokenType

    private val _currentTokenTypeStudentId = MutableStateFlow(Pair("", ""))
    internal val currentTokenTypeStudentId get() = _currentTokenTypeStudentId

    private val _currentClass = MutableStateFlow<Student?>(null)
    internal val currentClass get() = _currentClass

    init {
        getMasterScoolYear()
        getStudentClass()
        getLocal()
    }

    fun getExams(
        token: String = "",
        tahunAjaranId: Int = 0,
        jenisNilaiId: Int = 0,
        kelasId: Int = 0,
        matpelId: Int = 0,
        isGrouBy: Int = 0,
        type: ExamScreenType
    ) {
        viewModelScope.launch {
            examUseCase.execute(
                ExamListUseCase.Params(
                    token = token,
                    tahunAjaranSemesterId = tahunAjaranId,
                    kelasId = kelasId,
                    matpelId = matpelId,
                    jeniUjianId = jenisNilaiId,
                    isGroupBy = isGrouBy
                )
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        when(type) {
                            ExamScreenType.DETAIL -> _examDetailScreenState.value = ExamScreenState.Loading
                            ExamScreenType.LIST -> _examScreenState.value = ExamScreenState.Loading
                        }

                    }
                    is Resource.Success -> {
                        when(type) {
                            ExamScreenType.DETAIL -> _examDetailScreenState.value = ExamScreenState.Success(result.data)
                            ExamScreenType.LIST ->_examScreenState.value = ExamScreenState.Success(result.data)
                        }
                    }
                    is Resource.Error -> {
                        when(type) {
                            ExamScreenType.DETAIL -> _examDetailScreenState.value = ExamScreenState.Error(result.msg)
                            ExamScreenType.LIST -> _examScreenState.value = ExamScreenState.Error(result.msg)
                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getLocal() {
        viewModelScope.launch {
            userUseCase.getCurrentToken().onEach { token ->
                _currentToken.value = token
            }.flatMapConcat {
                userUseCase.getCurrentTokenType().onEach { tokenType ->
                    _currentTokenType.value = tokenType
                }
            }.collect {
                combine(
                    currentToken,
                    currentTokenType
                ) { token, tokenType ->
                    _currentTokenTypeStudentId.value = Pair(token, tokenType)
                }.flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Eagerly, null)
            }
        }
    }

    private fun getMasterScoolYear() {
        viewModelScope.launch {
            masterUseCase.getMasterSchoolYears()
                .collect { schoolYear ->
                    _masterSchoolYears.value = schoolYear.data.sortedByDescending { it.tahunAjaran }
                }
        }
    }

    private fun getStudentClass() {
        viewModelScope.launch {
            //_currentClass.value = masterUseCase.getStudentClass().data
        }
    }

}
