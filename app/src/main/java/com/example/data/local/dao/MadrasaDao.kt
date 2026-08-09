package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.ExamResultEntity
import com.example.data.local.entity.FeePaymentEntity
import com.example.data.local.entity.HifzProgressEntity
import com.example.data.local.entity.MadrasaFundEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MadrasaDao {

    // --- USERS ---
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    // --- STUDENTS ---
    @Query("SELECT * FROM students ORDER BY id DESC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE className = :className ORDER BY name ASC")
    fun getStudentsByClass(className: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentById(studentId: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE phoneNumber = :phone OR fatherName LIKE '%' || :phone || '%'")
    fun getStudentsByParentPhone(phone: String): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    // --- TEACHERS ---
    @Query("SELECT * FROM teachers ORDER BY name ASC")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity): Long

    @Update
    suspend fun updateTeacher(teacher: TeacherEntity)

    @Query("DELETE FROM teachers WHERE id = :id")
    suspend fun deleteTeacherById(id: Long)

    // --- ATTENDANCE ---
    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY className ASC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: Long): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendanceList: List<AttendanceEntity>)

    // --- FEES ---
    @Query("SELECT * FROM fee_payments ORDER BY paymentDate DESC")
    fun getAllFeePayments(): Flow<List<FeePaymentEntity>>

    @Query("SELECT * FROM fee_payments WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeePaymentsByStudent(studentId: Long): Flow<List<FeePaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeePayment(feePayment: FeePaymentEntity)

    // --- HIFZ PROGRESS ---
    @Query("SELECT * FROM hifz_progress ORDER BY date DESC")
    fun getAllHifzProgress(): Flow<List<HifzProgressEntity>>

    @Query("SELECT * FROM hifz_progress WHERE studentId = :studentId ORDER BY date DESC")
    fun getHifzProgressByStudent(studentId: Long): Flow<List<HifzProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHifzProgress(progress: HifzProgressEntity)

    // --- EXAMS ---
    @Query("SELECT * FROM exam_results ORDER BY date DESC")
    fun getAllExamResults(): Flow<List<ExamResultEntity>>

    @Query("SELECT * FROM exam_results WHERE studentId = :studentId ORDER BY date DESC")
    fun getExamResultsByStudent(studentId: Long): Flow<List<ExamResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResultEntity)

    // --- FUND ---
    @Query("SELECT * FROM madrasa_fund ORDER BY id DESC")
    fun getAllFundEntries(): Flow<List<MadrasaFundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundEntry(entry: MadrasaFundEntity)
}
