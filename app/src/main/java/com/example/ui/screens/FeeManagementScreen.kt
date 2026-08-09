package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FeePaymentEntity
import com.example.data.local.entity.MadrasaFundEntity
import com.example.data.local.entity.StudentEntity
import com.example.ui.components.IslamicStatCard
import com.example.ui.language.AppStrings
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.OnGoldContainer
import com.example.ui.viewmodel.MadrasaViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun FeeManagementScreen(
    viewModel: MadrasaViewModel,
    onBack: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val students by viewModel.allStudents.collectAsState()
    val feePayments by viewModel.allFeePayments.collectAsState()
    val fundEntries by viewModel.allFundEntries.collectAsState()
    val teachers by viewModel.allTeachers.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Excel Fee Ledger, 1: Madrasa Fund, 2: Teacher Salaries
    var showRecordPaymentDialog by remember { mutableStateOf<StudentEntity?>(null) }
    var showAddFundDialog by remember { mutableStateOf(false) }

    val totalMonthlyTarget = students.sumOf { it.monthlyFee }
    val totalCollected = feePayments.sumOf { it.amountPaid }
    val remainingDue = totalMonthlyTarget - totalCollected

    val totalIncome = fundEntries.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = fundEntries.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val fundBalance = totalIncome - totalExpense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Stats Banner
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IslamicStatCard(
                        title = "Total Collection",
                        value = "Rs. ${totalCollected.toInt()}",
                        subtext = "August Target: ${totalMonthlyTarget.toInt()}",
                        icon = Icons.Default.MonetizationOn,
                        containerColor = EmeraldContainer,
                        contentColor = OnEmeraldContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IslamicStatCard(
                        title = "Remaining Due",
                        value = "Rs. ${remainingDue.toInt()}",
                        subtext = "${students.count { !it.isFeePaid }} Pending",
                        icon = Icons.Default.ReceiptLong,
                        containerColor = GoldContainer,
                        contentColor = OnGoldContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Navigation Tabs
        PrimaryTabRow(selectedTabIndex = activeTab) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Excel Fee Ledger") },
                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Madrasa Fund") },
                icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Teacher Salaries") },
                icon = { Icon(Icons.Default.Payments, contentDescription = null) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
                0 -> ExcelFeeLedgerView(
                    students = students,
                    feePayments = feePayments,
                    onRecordPayment = { student -> showRecordPaymentDialog = student }
                )
                1 -> MadrasaFundLedgerView(
                    fundEntries = fundEntries,
                    fundBalance = fundBalance,
                    onAddFundClick = { showAddFundDialog = true }
                )
                2 -> TeacherSalariesView(teachers = teachers)
            }
        }

        // Dialogs
        showRecordPaymentDialog?.let { student ->
            RecordPaymentDialog(
                student = student,
                viewModel = viewModel,
                onDismiss = { showRecordPaymentDialog = null }
            )
        }

        if (showAddFundDialog) {
            AddFundEntryDialog(
                viewModel = viewModel,
                onDismiss = { showAddFundDialog = false }
            )
        }
    }
}

@Composable
fun ExcelFeeLedgerView(
    students: List<StudentEntity>,
    feePayments: List<FeePaymentEntity>,
    onRecordPayment: (StudentEntity) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Excel Table Horizontal Scroll Wrapper
        Box(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(scrollState)
        ) {
            Column {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .background(EmeraldPrimary)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Student Name", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(140.dp))
                    Text("Class", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                    Text("Monthly Fee", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                    Text("Paid Amount", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                    Text("Remaining Fee", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                    Text("Status", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                    Text("Action", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                }

                // Table Rows
                LazyColumn {
                    items(students) { student ->
                        val payment = feePayments.find { it.studentId == student.id }
                        val paid = payment?.amountPaid ?: 0.0
                        val remaining = student.monthlyFee - paid

                        Row(
                            modifier = Modifier
                                .background(if (student.id % 2L == 0L) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(student.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(140.dp))
                            Text(student.className, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(120.dp))
                            Text("Rs. ${student.monthlyFee.toInt()}", modifier = Modifier.width(100.dp))
                            Text("Rs. ${paid.toInt()}", color = EmeraldPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                            Text("Rs. ${remaining.toInt()}", color = if (remaining > 0) Color.Red else Color.Gray, modifier = Modifier.width(110.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (student.isFeePaid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                modifier = Modifier.width(90.dp)
                            ) {
                                Text(
                                    text = if (student.isFeePaid) "PAID" else "PENDING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (student.isFeePaid) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Button(
                                onClick = { onRecordPayment(student) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier
                                    .width(110.dp)
                                    .testTag("record_fee_btn_${student.id}")
                            ) {
                                Text("Pay Fee", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MadrasaFundLedgerView(
    fundEntries: List<MadrasaFundEntity>,
    fundBalance: Double,
    onAddFundClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Total Madrasa Fund Reserve", color = GoldAccent, style = MaterialTheme.typography.labelMedium)
                        Text("Rs. ${fundBalance.toInt()}", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Managed securely for Badaber Madrasa Siddiqiya", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                }
            }

            items(fundEntries) { entry ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(entry.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Category: ${entry.category} | Ref: ${entry.reference}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Date: ${entry.date}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }

                        Text(
                            text = "${if (entry.type == "INCOME") "+" else "-"} Rs. ${entry.amount.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            color = if (entry.type == "INCOME") EmeraldPrimary else Color.Red,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddFundClick,
            containerColor = GoldAccent,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_fund_entry_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Fund Entry")
        }
    }
}

@Composable
fun TeacherSalariesView(
    teachers: List<com.example.data.local.entity.TeacherEntity>
) {
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(teachers) { teacher ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(teacher.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Assigned: ${teacher.assignedClass}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Monthly Salary: Rs. ${teacher.salary.toInt()}", style = MaterialTheme.typography.bodySmall, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "PAID (Rs. ${teacher.paidSalaryThisMonth.toInt()})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecordPaymentDialog(
    student: StudentEntity,
    viewModel: MadrasaViewModel,
    onDismiss: () -> Unit
) {
    var amountPaidStr by remember { mutableStateOf(student.monthlyFee.toString()) }
    var receiptNo by remember { mutableStateOf("REC-2026-${System.currentTimeMillis().toString().takeLast(4)}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fee Receipt - ${student.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Class: ${student.className}", style = MaterialTheme.typography.bodyMedium)
                Text("Monthly Fee Due: Rs. ${student.monthlyFee.toInt()}", fontWeight = FontWeight.Bold, color = EmeraldPrimary)

                OutlinedTextField(
                    value = amountPaidStr,
                    onValueChange = { amountPaidStr = it },
                    label = { Text("Amount Paid (Rs.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = receiptNo,
                    onValueChange = { receiptNo = it },
                    label = { Text("Receipt Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountPaid = amountPaidStr.toDoubleOrNull() ?: student.monthlyFee
                    viewModel.recordFeePayment(
                        FeePaymentEntity(
                            studentId = student.id,
                            studentName = student.name,
                            month = "August 2026",
                            amountPaid = amountPaid,
                            totalDue = student.monthlyFee,
                            remainingFee = (student.monthlyFee - amountPaid).coerceAtLeast(0.0),
                            paymentDate = viewModel.todayDateStr,
                            receiptNo = receiptNo
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Generate Receipt")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddFundEntryDialog(
    viewModel: MadrasaViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Donation") }
    var type by remember { mutableStateOf("INCOME") } // INCOME or EXPENSE

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Madrasa Fund Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { type = "INCOME" },
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (type == "INCOME") EmeraldContainer else Color.Transparent),
                        modifier = Modifier.weight(1f)
                    ) { Text("Income (+)") }
                    OutlinedButton(
                        onClick = { type = "EXPENSE" },
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (type == "EXPENSE") Color(0xFFFFEBEE) else Color.Transparent),
                        modifier = Modifier.weight(1f)
                    ) { Text("Expense (-)") }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (Rs.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g., Donation, Utility)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && amountStr.isNotBlank()) {
                        viewModel.addFundEntry(
                            MadrasaFundEntity(
                                date = viewModel.todayDateStr,
                                type = type,
                                title = title,
                                amount = amountStr.toDoubleOrNull() ?: 0.0,
                                category = category,
                                reference = "FUND-${System.currentTimeMillis().toString().takeLast(4)}"
                            )
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) { Text("Save Fund Entry") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
