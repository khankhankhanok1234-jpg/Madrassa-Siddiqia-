package com.example.data.repository

import com.example.data.local.dao.MadrasaDao
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.ExamResultEntity
import com.example.data.local.entity.FeePaymentEntity
import com.example.data.local.entity.HifzProgressEntity
import com.example.data.local.entity.MadrasaFundEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class MadrasaRepository(private val dao: MadrasaDao) {

    // Users
    suspend fun getUserById(id: String): UserEntity? = dao.getUserById(id)
    suspend fun insertUser(user: UserEntity) = dao.insertUser(user)
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()

    // Students
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    fun getStudentsByClass(className: String): Flow<List<StudentEntity>> = dao.getStudentsByClass(className)
    fun getStudentsByParentPhone(phone: String): Flow<List<StudentEntity>> = dao.getStudentsByParentPhone(phone)
    suspend fun getStudentById(id: Long): StudentEntity? = dao.getStudentById(id)
    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)
    suspend fun deleteStudentById(id: Long) = dao.deleteStudentById(id)
    val studentCount: Flow<Int> = dao.getStudentCount()

    // Teachers
    val allTeachers: Flow<List<TeacherEntity>> = dao.getAllTeachers()
    suspend fun insertTeacher(teacher: TeacherEntity): Long = dao.insertTeacher(teacher)
    suspend fun updateTeacher(teacher: TeacherEntity) = dao.updateTeacher(teacher)
    suspend fun deleteTeacherById(id: Long) = dao.deleteTeacherById(id)

    // Attendance
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>> = dao.getAttendanceByDate(date)
    fun getAttendanceByStudent(studentId: Long): Flow<List<AttendanceEntity>> = dao.getAttendanceByStudent(studentId)
    suspend fun saveAttendance(attendanceList: List<AttendanceEntity>) = dao.insertAttendance(attendanceList)

    // Fees
    val allFeePayments: Flow<List<FeePaymentEntity>> = dao.getAllFeePayments()
    fun getFeePaymentsByStudent(studentId: Long): Flow<List<FeePaymentEntity>> = dao.getFeePaymentsByStudent(studentId)
    suspend fun insertFeePayment(feePayment: FeePaymentEntity) = dao.insertFeePayment(feePayment)

    // Daily Hifz Progress
    val allHifzProgress: Flow<List<HifzProgressEntity>> = dao.getAllHifzProgress()
    fun getHifzProgressByStudent(studentId: Long): Flow<List<HifzProgressEntity>> = dao.getHifzProgressByStudent(studentId)
    suspend fun insertHifzProgress(progress: HifzProgressEntity) = dao.insertHifzProgress(progress)

    // Exams
    val allExamResults: Flow<List<ExamResultEntity>> = dao.getAllExamResults()
    fun getExamResultsByStudent(studentId: Long): Flow<List<ExamResultEntity>> = dao.getExamResultsByStudent(studentId)
    suspend fun insertExamResult(result: ExamResultEntity) = dao.insertExamResult(result)

    // Fund
    val allFundEntries: Flow<List<MadrasaFundEntity>> = dao.getAllFundEntries()
    suspend fun insertFundEntry(entry: MadrasaFundEntity) = dao.insertFundEntry(entry)
}
