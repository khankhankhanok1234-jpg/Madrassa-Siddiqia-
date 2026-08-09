package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    ADMIN, TEACHER, PARENT
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // Username / Phone Number / "admin"
    val name: String,
    val role: UserRole,
    val pin: String,
    val assignedClass: String? = null,
    val linkedStudentId: Long? = null,
    val parentPhone: String? = null
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val fatherName: String,
    val phoneNumber: String,
    val address: String,
    val admissionDate: String,
    val className: String, // "Qaida Class A", "Qaida Class B", "Nazira Class A", "Nazira Class B", "Nazira Class C", "Hifz Quran Class A", "Hifz Quran Class B"
    val category: String, // "Qaida", "Nazira", "Hifz"
    val monthlyFee: Double = 1000.0,
    val isFeePaid: Boolean = false,
    val status: String = "Active" // "Active", "Promoted", "Graduated"
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val assignedClass: String,
    val qualification: String,
    val salary: Double,
    val paidSalaryThisMonth: Double = 0.0,
    val joinDate: String
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val date: String, // YYYY-MM-DD
    val status: String, // "Present", "Absent", "Late", "Leave"
    val teacherName: String = "System"
)

@Entity(tableName = "fee_payments")
data class FeePaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val month: String, // e.g., "August 2026"
    val amountPaid: Double,
    val totalDue: Double,
    val remainingFee: Double,
    val paymentDate: String,
    val receiptNo: String,
    val notes: String = ""
)

@Entity(tableName = "hifz_progress")
data class HifzProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val date: String,
    val sabaq: String, // New Lesson
    val sabqi: String, // Previous Lesson
    val manzil: String, // Revision
    val remarks: String = "Excellent",
    val teacherName: String
)

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val examType: String, // "Monthly Assessment", "Two Month Evaluation", "Annual Examination"
    val category: String, // "Qaida", "Nazira", "Hifz Quran"
    val marksObtained: Double,
    val totalMarks: Double = 100.0,
    val grade: String, // "A+", "A", "B", "C"
    val remarks: String,
    val date: String
)

@Entity(tableName = "madrasa_fund")
data class MadrasaFundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val type: String, // "INCOME", "EXPENSE"
    val title: String,
    val amount: Double,
    val category: String, // "Donation", "Fee Collection", "Utility", "Maintenance", "Teacher Salary", "Other"
    val reference: String = ""
)
