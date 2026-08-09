package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.UserRole
import com.example.ui.components.IslamicStatCard
import com.example.ui.components.SectionHeader
import com.example.ui.language.AppStrings
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.TileAmberBg
import com.example.ui.theme.TileAmberIcon
import com.example.ui.theme.TileBlueBg
import com.example.ui.theme.TileBlueIcon
import com.example.ui.theme.TileEmeraldBg
import com.example.ui.theme.TileEmeraldIcon
import com.example.ui.theme.TileIndigoBg
import com.example.ui.theme.TileIndigoIcon
import com.example.ui.theme.TileRoseBg
import com.example.ui.theme.TileRoseIcon
import com.example.ui.theme.TileSlateBg
import com.example.ui.theme.TileSlateIcon
import com.example.ui.viewmodel.MadrasaViewModel

data class DashboardCardItem(
    val id: String,
    val titleKey: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconColor: Color,
    val adminOnly: Boolean = false
)

@Composable
fun DashboardScreen(
    viewModel: MadrasaViewModel,
    onNavigate: (String) -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val students by viewModel.allStudents.collectAsState()
    val teachers by viewModel.allTeachers.collectAsState()
    val attendanceList by viewModel.attendanceForSelectedDate.collectAsState()
    val feePayments by viewModel.allFeePayments.collectAsState()
    val fundEntries by viewModel.allFundEntries.collectAsState()

    val presentCount = attendanceList.count { it.status == "Present" }
    val totalFeeCollected = feePayments.sumOf { it.amountPaid }
    val totalIncome = fundEntries.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = fundEntries.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val fundBalance = totalIncome - totalExpense

    val dashboardCards = listOf(
        DashboardCardItem("students", "students", "${students.size} Enrolled", Icons.Default.People, TileBlueBg, TileBlueIcon),
        DashboardCardItem("attendance", "attendance", "Daily Register", Icons.Default.CalendarMonth, TileEmeraldBg, TileEmeraldIcon),
        DashboardCardItem("fees", "fees", "Excel Fee Ledger", Icons.Default.MonetizationOn, TileAmberBg, TileAmberIcon, adminOnly = true),
        DashboardCardItem("exams", "exams", "Results & Grades", Icons.Default.Assessment, TileIndigoBg, TileIndigoIcon),
        DashboardCardItem("reports", "reports", "Screen Export", Icons.Default.MenuBook, TileRoseBg, TileRoseIcon),
        DashboardCardItem("settings", "settings", "Backup & Language", Icons.Default.Settings, TileSlateBg, TileSlateIcon)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image Banner & Bismillah Greeting
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_madrasa_banner),
                contentDescription = "Madrasa Hero Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "بِسْمِ ٱللَّٰهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${AppStrings.get("app_title", language)} - ${AppStrings.get("location", language)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Classes: 2 Hifz • 3 Nazira • 2 Qaida | 8 Quran Teachers",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IslamicStatCard(
                title = AppStrings.get("total_students", language),
                value = "${students.size}",
                subtext = "Active Admissions",
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            IslamicStatCard(
                title = AppStrings.get("present_today", language),
                value = "$presentCount / ${students.size}",
                subtext = "Attendance Recorded",
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f)
            )
        }

        if (user.role == UserRole.ADMIN) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IslamicStatCard(
                    title = AppStrings.get("fee_collection", language),
                    value = "Rs. ${totalFeeCollected.toInt()}",
                    subtext = "August Payments",
                    icon = Icons.Default.MonetizationOn,
                    containerColor = GoldContainer,
                    contentColor = OnGoldContainer,
                    modifier = Modifier.weight(1f)
                )
                IslamicStatCard(
                    title = AppStrings.get("fund_balance", language),
                    value = "Rs. ${fundBalance.toInt()}",
                    subtext = "Madrasa Reserve",
                    icon = Icons.Default.School,
                    containerColor = EmeraldContainer,
                    contentColor = OnEmeraldContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Main Dashboard Modules
        SectionHeader(title = "Madrasa Siddiqiya Management")

        // 6 Main Sleek Dashboard Cards
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val visibleCards = dashboardCards.filter { !it.adminOnly || user.role == UserRole.ADMIN }
            visibleCards.chunked(2).forEach { rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCards.forEach { card ->
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clickable { onNavigate(card.id) }
                                .testTag("dashboard_card_${card.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(card.iconBgColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = card.icon,
                                            contentDescription = null,
                                            tint = card.iconColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    if (card.adminOnly) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = GoldAccent.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Admin",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = GoldAccent,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Column {
                                    Text(
                                        text = AppStrings.get(card.titleKey, language),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = card.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                    if (rowCards.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Hifz Target Banner (Matching Sleek Interface Footer Card)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onNavigate("daily_hifz") }
                .testTag("sleek_hifz_banner")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Hifz Target",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { 0.72f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GoldAccent,
                        trackColor = Color.Black.copy(alpha = 0.25f),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "32 students completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD1FAE5),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "72%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD1FAE5),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Action Shortcuts
        SectionHeader(title = "Specialized Quick Portals")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = EmeraldContainer,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate("daily_hifz") }
                    .testTag("quick_hifz_portal")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("daily_hifz", language),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnEmeraldContainer
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GoldContainer,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate("teachers") }
                    .testTag("quick_teachers_portal")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = OnGoldContainer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${AppStrings.get("teachers", language)} (${teachers.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnGoldContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
