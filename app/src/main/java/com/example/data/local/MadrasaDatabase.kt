package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.MadrasaDao
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.ExamResultEntity
import com.example.data.local.entity.FeePaymentEntity
import com.example.data.local.entity.HifzProgressEntity
import com.example.data.local.entity.MadrasaFundEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        TeacherEntity::class,
        AttendanceEntity::class,
        FeePaymentEntity::class,
        HifzProgressEntity::class,
        ExamResultEntity::class,
        MadrasaFundEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MadrasaDatabase : RoomDatabase() {

    abstract fun madrasaDao(): MadrasaDao

    companion object {
        @Volatile
        private var INSTANCE: MadrasaDatabase? = null

        fun getDatabase(context: Context): MadrasaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MadrasaDatabase::class.java,
                    "madrasa_siddiqiya_db"
                )
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database.madrasaDao())
                    }
                }
            }

            private suspend fun seedDatabase(dao: MadrasaDao) {
                // Seed Default Users
                dao.insertUser(
                    UserEntity(
                        id = "admin",
                        name = "Principal Administrator",
                        role = UserRole.ADMIN,
                        pin = "1234"
                    )
                )
                dao.insertUser(
                    UserEntity(
                        id = "teacher",
                        name = "Qari Muhammad Bilal",
                        role = UserRole.TEACHER,
                        pin = "5555",
                        assignedClass = "Hifz Quran Class A"
                    )
                )
                dao.insertUser(
                    UserEntity(
                        id = "03001234567",
                        name = "Haji Gul Khan (Parent)",
                        role = UserRole.PARENT,
                        pin = "9999",
                        parentPhone = "03001234567"
                    )
                )

                // Seed 8 Quran Teachers
                val teacherList = listOf(
                    TeacherEntity(name = "Qari Muhammad Bilal", phoneNumber = "03001111111", assignedClass = "Hifz Quran Class A", qualification = "Hafiz & Qari (Wafaq-ul-Madaris)", salary = 35000.0, paidSalaryThisMonth = 35000.0, joinDate = "2022-01-15"),
                    TeacherEntity(name = "Qari Hafiz Ahmad", phoneNumber = "03002222222", assignedClass = "Hifz Quran Class B", qualification = "Hafiz & Shahadat-ul-Aalamia", salary = 34000.0, paidSalaryThisMonth = 34000.0, joinDate = "2022-03-01"),
                    TeacherEntity(name = "Qari Saeed Khan", phoneNumber = "03003333333", assignedClass = "Nazira Class A", qualification = "Qari & Tajweed Specialist", salary = 28000.0, paidSalaryThisMonth = 28000.0, joinDate = "2023-01-10"),
                    TeacherEntity(name = "Qari Zia-ur-Rahman", phoneNumber = "03004444444", assignedClass = "Nazira Class B", qualification = "Qari & Islamic Studies", salary = 27000.0, paidSalaryThisMonth = 27000.0, joinDate = "2023-05-12"),
                    TeacherEntity(name = "Qari Ihsanullah", phoneNumber = "03005555555", assignedClass = "Nazira Class C", qualification = "Tajweed & Qirat Master", salary = 26000.0, paidSalaryThisMonth = 26000.0, joinDate = "2023-08-20"),
                    TeacherEntity(name = "Qari Abdul Rahim", phoneNumber = "03006666666", assignedClass = "Qaida Class A", qualification = "Qaida & Initial Phonetics", salary = 24000.0, paidSalaryThisMonth = 24000.0, joinDate = "2024-02-01"),
                    TeacherEntity(name = "Qari Saifuddin", phoneNumber = "03007777777", assignedClass = "Qaida Class B", qualification = "Basic Quranic Studies", salary = 24000.0, paidSalaryThisMonth = 24000.0, joinDate = "2024-04-15"),
                    TeacherEntity(name = "Qari Tariq Mahmood", phoneNumber = "03008888888", assignedClass = "Hifz Assistant", qualification = "Hafiz Quran", salary = 22000.0, paidSalaryThisMonth = 22000.0, joinDate = "2024-06-01")
                )
                teacherList.forEach { dao.insertTeacher(it) }

                // Seed Students
                val students = listOf(
                    StudentEntity(name = "Abdullah Gul", fatherName = "Haji Gul Khan", phoneNumber = "03001234567", address = "Main Market, Badaber, Peshawar", admissionDate = "2023-03-10", className = "Hifz Quran Class A", category = "Hifz", monthlyFee = 1200.0, isFeePaid = true),
                    StudentEntity(name = "Muhammad Umar", fatherName = "Haji Gul Khan", phoneNumber = "03001234567", address = "Main Market, Badaber, Peshawar", admissionDate = "2024-01-15", className = "Nazira Class A", category = "Nazira", monthlyFee = 1000.0, isFeePaid = true),
                    StudentEntity(name = "Zubair Ahmad", fatherName = "Ahmad Shah", phoneNumber = "03019876543", address = "Bypass Road, Badaber, Peshawar", admissionDate = "2023-05-20", className = "Hifz Quran Class A", category = "Hifz", monthlyFee = 1200.0, isFeePaid = false),
                    StudentEntity(name = "Usman Ali", fatherName = "Sher Ali Khan", phoneNumber = "03023456789", address = "Village Sheikhan, Badaber", admissionDate = "2024-02-10", className = "Nazira Class B", category = "Nazira", monthlyFee = 1000.0, isFeePaid = true),
                    StudentEntity(name = "Hamza Ihsan", fatherName = "Ihsanullah Jan", phoneNumber = "03034567890", address = "Near Grid Station, Badaber", admissionDate = "2024-04-01", className = "Qaida Class A", category = "Qaida", monthlyFee = 800.0, isFeePaid = true),
                    StudentEntity(name = "Bilal Tariq", fatherName = "Tariq Aziz", phoneNumber = "03045678901", address = "Kohat Road, Badaber, Peshawar", admissionDate = "2024-05-12", className = "Qaida Class B", category = "Qaida", monthlyFee = 800.0, isFeePaid = false),
                    StudentEntity(name = "Abu Bakr", fatherName = "Malik Rashid", phoneNumber = "03056789012", address = "Afridi Garhi, Badaber", admissionDate = "2022-11-05", className = "Hifz Quran Class B", category = "Hifz", monthlyFee = 1200.0, isFeePaid = true)
                )

                val studentIds = mutableListOf<Long>()
                students.forEach {
                    val id = dao.insertStudent(it)
                    studentIds.add(id)
                }

                val today = "2026-08-06"
                // Seed Attendance
                val attendanceEntries = listOf(
                    AttendanceEntity(studentId = studentIds[0], studentName = "Abdullah Gul", className = "Hifz Quran Class A", date = today, status = "Present", teacherName = "Qari Muhammad Bilal"),
                    AttendanceEntity(studentId = studentIds[1], studentName = "Muhammad Umar", className = "Nazira Class A", date = today, status = "Present", teacherName = "Qari Saeed Khan"),
                    AttendanceEntity(studentId = studentIds[2], studentName = "Zubair Ahmad", className = "Hifz Quran Class A", date = today, status = "Absent", teacherName = "Qari Muhammad Bilal"),
                    AttendanceEntity(studentId = studentIds[3], studentName = "Usman Ali", className = "Nazira Class B", date = today, status = "Present", teacherName = "Qari Zia-ur-Rahman"),
                    AttendanceEntity(studentId = studentIds[4], studentName = "Hamza Ihsan", className = "Qaida Class A", date = today, status = "Present", teacherName = "Qari Abdul Rahim")
                )
                dao.insertAttendance(attendanceEntries)

                // Seed Daily Hifz Progress
                dao.insertHifzProgress(
                    HifzProgressEntity(
                        studentId = studentIds[0],
                        studentName = "Abdullah Gul",
                        className = "Hifz Quran Class A",
                        date = today,
                        sabaq = "Para 14 - Surah Al-Hijr (15 Verses)",
                        sabqi = "Para 13 (Half Para)",
                        manzil = "Para 1 to Para 3",
                        remarks = "Excellent Memorization (Mumtaz)",
                        teacherName = "Qari Muhammad Bilal"
                    )
                )
                dao.insertHifzProgress(
                    HifzProgressEntity(
                        studentId = studentIds[1],
                        studentName = "Muhammad Umar",
                        className = "Nazira Class A",
                        date = today,
                        sabaq = "Surah Yaseen (Verses 1 - 25 with Tajweed)",
                        sabqi = "Surah Al-Waqiah",
                        manzil = "Para 29 Revision",
                        remarks = "Good Pronunciation",
                        teacherName = "Qari Saeed Khan"
                    )
                )

                // Seed Fee Payments
                dao.insertFeePayment(
                    FeePaymentEntity(
                        studentId = studentIds[0],
                        studentName = "Abdullah Gul",
                        month = "August 2026",
                        amountPaid = 1200.0,
                        totalDue = 1200.0,
                        remainingFee = 0.0,
                        paymentDate = today,
                        receiptNo = "REC-2026-001",
                        notes = "Paid via Cash"
                    )
                )

                // Seed Exam Results
                dao.insertExamResult(
                    ExamResultEntity(
                        studentId = studentIds[0],
                        studentName = "Abdullah Gul",
                        className = "Hifz Quran Class A",
                        examType = "Two Month Evaluation",
                        category = "Hifz Quran",
                        marksObtained = 96.0,
                        totalMarks = 100.0,
                        grade = "A+",
                        remarks = "Flawless Tajweed and Strong Retention",
                        date = "2026-07-25"
                    )
                )
                dao.insertExamResult(
                    ExamResultEntity(
                        studentId = studentIds[1],
                        studentName = "Muhammad Umar",
                        className = "Nazira Class A",
                        examType = "Two Month Evaluation",
                        category = "Nazira",
                        marksObtained = 88.0,
                        totalMarks = 100.0,
                        grade = "A",
                        remarks = "Good Makharij, Keep Practicing Heavy Letters",
                        date = "2026-07-25"
                    )
                )

                // Seed Madrasa Fund Entries
                dao.insertFundEntry(
                    MadrasaFundEntity(
                        date = today,
                        type = "INCOME",
                        title = "Monthly Fee Collections",
                        amount = 185000.0,
                        category = "Fee Collection",
                        reference = "AUG-FEE-TOTAL"
                    )
                )
                dao.insertFundEntry(
                    MadrasaFundEntity(
                        date = today,
                        type = "INCOME",
                        title = "Generous Local Community Donation",
                        amount = 200000.0,
                        category = "Donation",
                        reference = "DON-BADABER-08"
                    )
                )
                dao.insertFundEntry(
                    MadrasaFundEntity(
                        date = today,
                        type = "EXPENSE",
                        title = "Electricity & Utility Bills",
                        amount = 45000.0,
                        category = "Utility",
                        reference = "PESCO-AUG-2026"
                    )
                )
            }
        }
    }
}
