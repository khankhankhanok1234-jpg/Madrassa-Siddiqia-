package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MadrasaDatabase
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.ExamResultEntity
import com.example.data.local.entity.FeePaymentEntity
import com.example.data.local.entity.HifzProgressEntity
import com.example.data.local.entity.MadrasaFundEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.UserRole
import com.example.data.repository.MadrasaRepository
import com.example.ui.language.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MadrasaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MadrasaRepository
    val todayDateStr: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Active Language
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Current User & Active Role
    private val _currentUser = MutableStateFlow(
        UserEntity(id = "admin", name = "Principal Administrator", role = UserRole.ADMIN, pin = "1234")
    )
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    // Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedClassFilter = MutableStateFlow("All")
    val selectedClassFilter: StateFlow<String> = _selectedClassFilter.asStateFlow()

    private val _selectedDate = MutableStateFlow(todayDateStr)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Cloud Sync & Backup Status
    private val _isCloudSynced = MutableStateFlow(true)
    val isCloudSynced: StateFlow<Boolean> = _isCloudSynced.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val dao = MadrasaDatabase.getDatabase(application).madrasaDao()
        repository = MadrasaRepository(dao)
    }

    // Repository Flows
    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredStudents: StateFlow<List<StudentEntity>> = combine(
        repository.allStudents,
        _searchQuery,
        _selectedClassFilter
    ) { students, query, classFilter ->
        students.filter { student ->
            val matchesQuery = query.isBlank() ||
                    student.name.contains(query, ignoreCase = true) ||
                    student.fatherName.contains(query, ignoreCase = true) ||
                    student.phoneNumber.contains(query, ignoreCase = true)
            val matchesClass = classFilter == "All" || student.className == classFilter
            matchesQuery && matchesClass
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeachers: StateFlow<List<TeacherEntity>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFeePayments: StateFlow<List<FeePaymentEntity>> = repository.allFeePayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHifzProgress: StateFlow<List<HifzProgressEntity>> = repository.allHifzProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExamResults: StateFlow<List<ExamResultEntity>> = repository.allExamResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFundEntries: StateFlow<List<MadrasaFundEntity>> = repository.allFundEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceForSelectedDate: StateFlow<List<AttendanceEntity>> = repository.getAttendanceByDate(todayDateStr)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun switchUserRole(role: UserRole) {
        when (role) {
            UserRole.ADMIN -> _currentUser.value = UserEntity(id = "admin", name = "Principal Administrator", role = UserRole.ADMIN, pin = "1234")
            UserRole.TEACHER -> _currentUser.value = UserEntity(id = "teacher", name = "Qari Muhammad Bilal", role = UserRole.TEACHER, pin = "5555", assignedClass = "Hifz Quran Class A")
            UserRole.PARENT -> _currentUser.value = UserEntity(id = "03001234567", name = "Haji Gul Khan (Parent)", role = UserRole.PARENT, pin = "9999", parentPhone = "03001234567")
        }
        showToast("Switched to ${role.name} Mode")
    }

    fun loginWithPin(idOrPhone: String, pin: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserById(idOrPhone)
            if (user != null && user.pin == pin) {
                _currentUser.value = user
                onResult(true, null)
            } else {
                // Fallback quick logins for convenience
                if (pin == "1234") {
                    switchUserRole(UserRole.ADMIN)
                    onResult(true, null)
                } else if (pin == "5555") {
                    switchUserRole(UserRole.TEACHER)
                    onResult(true, null)
                } else if (pin == "9999") {
                    switchUserRole(UserRole.PARENT)
                    onResult(true, null)
                } else {
                    onResult(false, "Invalid Security PIN")
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setClassFilter(className: String) {
        _selectedClassFilter.value = className
    }

    fun addStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.insertStudent(student)
            showToast("Student Added: ${student.name}")
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student)
            showToast("Student Updated: ${student.name}")
        }
    }

    fun promoteStudent(student: StudentEntity) {
        viewModelScope.launch {
            val nextClass = when (student.category) {
                "Qaida" -> "Nazira Class A"
                "Nazira" -> "Hifz Quran Class A"
                else -> "Graduated"
            }
            val nextCategory = when (student.category) {
                "Qaida" -> "Nazira"
                "Nazira" -> "Hifz"
                else -> "Graduated"
            }
            val updated = student.copy(
                className = nextClass,
                category = nextCategory,
                status = if (nextCategory == "Graduated") "Graduated" else "Promoted"
            )
            repository.updateStudent(updated)
            showToast("${student.name} Promoted to $nextClass")
        }
    }

    fun addTeacher(teacher: TeacherEntity) {
        viewModelScope.launch {
            repository.insertTeacher(teacher)
            showToast("Teacher Added: ${teacher.name}")
        }
    }

    fun saveAttendance(attendanceList: List<AttendanceEntity>) {
        viewModelScope.launch {
            repository.saveAttendance(attendanceList)
            showToast("Daily Attendance Recorded Successfully")
        }
    }

    fun recordFeePayment(feePayment: FeePaymentEntity) {
        viewModelScope.launch {
            repository.insertFeePayment(feePayment)
            // Mark student fee as paid
            val student = repository.getStudentById(feePayment.studentId)
            student?.let {
                repository.updateStudent(it.copy(isFeePaid = true))
            }
            // Add to Madrasa Fund income
            repository.insertFundEntry(
                MadrasaFundEntity(
                    date = feePayment.paymentDate,
                    type = "INCOME",
                    title = "Fee Collection - ${feePayment.studentName}",
                    amount = feePayment.amountPaid,
                    category = "Fee Collection",
                    reference = feePayment.receiptNo
                )
            )
            showToast("Fee Receipt Generated: ${feePayment.receiptNo}")
        }
    }

    fun logHifzProgress(progress: HifzProgressEntity) {
        viewModelScope.launch {
            repository.insertHifzProgress(progress)
            showToast("Daily Lesson Logged for ${progress.studentName}")
        }
    }

    fun recordExamResult(result: ExamResultEntity) {
        viewModelScope.launch {
            repository.insertExamResult(result)
            showToast("Exam Score Saved: ${result.studentName}")
        }
    }

    fun addFundEntry(entry: MadrasaFundEntity) {
        viewModelScope.launch {
            repository.insertFundEntry(entry)
            showToast("${entry.type} Entry Added: Rs. ${entry.amount}")
        }
    }

    fun toggleCloudSync() {
        _isCloudSynced.value = !_isCloudSynced.value
        showToast(if (_isCloudSynced.value) "Cloud Synchronization Active" else "Offline Local Mode Active")
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
