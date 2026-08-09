package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.entity.UserRole
import com.example.ui.components.MadrasaTopBar
import com.example.ui.language.AppStrings
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.ClassesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamResultsScreen
import com.example.ui.screens.FeeManagementScreen
import com.example.ui.screens.HifzProgressScreen
import com.example.ui.screens.ParentPortalScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.screens.TeachersScreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MadrasaSiddiqiyaTheme
import com.example.ui.viewmodel.MadrasaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MadrasaSiddiqiyaTheme {
                MadrasaAppMain()
            }
        }
    }
}

@Composable
fun MadrasaAppMain() {
    val viewModel: MadrasaViewModel = viewModel()
    val navController = rememberNavController()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isCloudSynced by viewModel.isCloudSynced.collectAsState()
    val toastMsg by viewModel.toastMessage.collectAsState()

    // Show Toast messages
    LaunchedEffect(toastMsg) {
        toastMsg?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        topBar = {
            MadrasaTopBar(
                currentLanguage = currentLanguage,
                currentUser = currentUser,
                isCloudSynced = isCloudSynced,
                onLanguageChange = { viewModel.setLanguage(it) },
                onRoleSwitch = { role ->
                    viewModel.switchUserRole(role)
                    if (role == UserRole.PARENT) {
                        navController.navigate("parent_portal") {
                            popUpTo("dashboard") { saveState = true }
                        }
                    } else if (role == UserRole.TEACHER) {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { saveState = true }
                        }
                    } else {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { saveState = true }
                        }
                    }
                },
                onToggleSync = { viewModel.toggleCloudSync() }
            )
        },
        bottomBar = {
            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = EmeraldPrimary,
                shadowElevation = 8.dp
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    // Dashboard
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text(AppStrings.get("dashboard", currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = Color.White,
                            indicatorColor = EmeraldContainer,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )

                    // Students
                    NavigationBarItem(
                        selected = currentRoute == "students",
                        onClick = {
                            navController.navigate("students") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.People, contentDescription = "Students") },
                        label = { Text(AppStrings.get("students", currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = Color.White,
                            indicatorColor = EmeraldContainer,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_item_students")
                    )

                    // Attendance
                    NavigationBarItem(
                        selected = currentRoute == "attendance",
                        onClick = {
                            navController.navigate("attendance") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Attendance") },
                        label = { Text(AppStrings.get("attendance", currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = Color.White,
                            indicatorColor = EmeraldContainer,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_item_attendance")
                    )

                    // Role Aware Tab: Fees (Admin/Teacher) vs Parent Portal (Parent)
                    if (currentUser.role == UserRole.PARENT) {
                        NavigationBarItem(
                            selected = currentRoute == "parent_portal",
                            onClick = {
                                navController.navigate("parent_portal") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(Icons.Default.Person, contentDescription = "My Children") },
                            label = { Text("My Children", fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = Color.White,
                                indicatorColor = EmeraldContainer,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag("nav_item_parent")
                        )
                    } else {
                        NavigationBarItem(
                            selected = currentRoute == "fees",
                            onClick = {
                                navController.navigate("fees") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Fees") },
                            label = { Text(AppStrings.get("fees", currentLanguage), fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = Color.White,
                                indicatorColor = EmeraldContainer,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag("nav_item_fees")
                        )
                    }

                    // Settings
                    NavigationBarItem(
                        selected = currentRoute == "settings",
                        onClick = {
                            navController.navigate("settings") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text(AppStrings.get("settings", currentLanguage), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = Color.White,
                            indicatorColor = EmeraldContainer,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = if (currentUser.role == UserRole.PARENT) "parent_portal" else "dashboard"
            ) {
                composable("dashboard") {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable("students") {
                    StudentsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("teachers") {
                    TeachersScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("classes") {
                    ClassesScreen(
                        viewModel = viewModel,
                        onSelectClass = { clsName ->
                            viewModel.setClassFilter(clsName)
                            navController.navigate("students")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("attendance") {
                    AttendanceScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("fees") {
                    FeeManagementScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("exams") {
                    ExamResultsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("daily_hifz") {
                    HifzProgressScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("reports") {
                    ReportsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("parent_portal") {
                    ParentPortalScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
