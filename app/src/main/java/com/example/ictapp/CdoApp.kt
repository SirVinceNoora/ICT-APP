package com.example.ictapp

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ictapp.speedtest.SettingsManager
import com.example.ictapp.ui.components.BlueGradientBackground
import com.example.ictapp.ui.components.DrawerHeader
import com.example.ictapp.ui.components.WelcomeDialog
import com.example.ictapp.ui.screens.*
import kotlinx.coroutines.launch

// --- NAVIGATION ---
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Troubleshooting : Screen("kb", "Knowledge Base", Icons.Default.MenuBook)
    object RepairLogs : Screen("logs", "Repair Logs", Icons.Default.Assignment)
    object NetworkTools : Screen("network", "Network Tools", Icons.Default.Router)
    object Deployment : Screen("deploy", "Deployment", Icons.Default.FactCheck)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CdoApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val settingsManager = remember { SettingsManager(context) }

    // Global State
    val repairLogs = remember { mutableStateListOf<RepairLog>() }
    val deploymentTasks = remember { mutableStateListOf<DeploymentTask>() }
    
    val showWelcomePersistent by settingsManager.showWelcome.collectAsState(initial = true)
    var showWelcomeSession by remember { mutableStateOf(true) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screenTitle = when {
        currentRoute == Screen.Dashboard.route -> "OVERVIEW"
        currentRoute == Screen.Troubleshooting.route -> "KNOWLEDGE BASE"
        currentRoute == Screen.RepairLogs.route -> "REPAIR TRACKER"
        currentRoute?.startsWith("network") == true -> "NETWORK TOOLS"
        currentRoute == Screen.Deployment.route -> "DEPLOYMENT"
        currentRoute == Screen.Settings.route -> "SETTINGS"
        currentRoute == "radar" -> "VICINITY RADAR"
        else -> ""
    }

    if (showWelcomePersistent && showWelcomeSession) {
        WelcomeDialog(
            onDismiss = { showWelcomeSession = false },
            onDontShowAgain = { scope.launch { settingsManager.setShowWelcome(false) } }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color(0xFF001F54)) {
                DrawerHeader()
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color.White.copy(0.1f))
                listOf(Screen.Dashboard, Screen.Troubleshooting, Screen.RepairLogs, Screen.NetworkTools, Screen.Deployment, Screen.Settings).forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = { 
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() } 
                        },
                        icon = { Icon(screen.icon, null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = Color.White, 
                            unselectedIconColor = Color.Cyan,
                            selectedContainerColor = Color.Cyan.copy(0.1f),
                            selectedTextColor = Color.Cyan,
                            selectedIconColor = Color.Cyan
                        )
                    )
                }
            }
        }
    ) {
        BlueGradientBackground {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Column(Modifier.fillMaxWidth().background(Color.White.copy(0.05f))) {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White),
                            title = { Text("ICT FIELD OPS", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                            navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null, tint = Color.White) } }
                        )
                        Box(Modifier.fillMaxWidth().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
                            Text(text = screenTitle, color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Dashboard.route,
                    modifier = Modifier.padding(padding),
                    enterTransition = { scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow), initialScale = 0.9f) + fadeIn() },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow), initialScale = 1.1f) + fadeIn() },
                    popExitTransition = { fadeOut() }
                ) {
                    composable(Screen.Dashboard.route) { DashboardContent(logs = repairLogs.size, deploys = deploymentTasks.size, onNavigate = { route -> navController.navigate(route) }) }
                    composable(Screen.Troubleshooting.route) { TroubleshootingScreen(onBack = { navController.popBackStack() }) }
                    composable(Screen.RepairLogs.route) { RepairLogScreen(repairLogs, onBack = { navController.popBackStack() }) }
                    composable(route = "network?tab={tab}", arguments = listOf(navArgument("tab") { defaultValue = "0" })) { backStackEntry ->
                        val initialTab = backStackEntry.arguments?.getString("tab")?.toIntOrNull() ?: 0
                        NetworkToolsScreen(initialTab = initialTab, onBack = { navController.popBackStack() })
                    }
                    composable(Screen.NetworkTools.route) { NetworkToolsScreen(initialTab = 0, onBack = { navController.popBackStack() }) }
                    composable(Screen.Deployment.route) { DeploymentScreen(deploymentTasks, onBack = { navController.popBackStack() }) }
                    composable(Screen.Settings.route) { SettingsScreen(settingsManager, onBack = { navController.popBackStack() }) }
                    composable("radar") { VicinityRadarScreen() }
                }
            }
        }
    }
}
