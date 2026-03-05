package com.example.ictapp

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.ictapp.ui.components.BlueGradientBackground
import com.example.ictapp.ui.components.DrawerHeader
import com.example.ictapp.ui.screens.DashboardContent
import com.example.ictapp.ui.screens.DeploymentScreen
import com.example.ictapp.ui.screens.NetworkToolsScreen
import com.example.ictapp.ui.screens.RepairLogScreen
import com.example.ictapp.ui.screens.TroubleshootingScreen
import kotlinx.coroutines.launch

// --- NAVIGATION ---
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Troubleshooting : Screen("kb", "Knowledge Base", Icons.Default.MenuBook)
    object RepairLogs : Screen("logs", "Repair Logs", Icons.Default.Assignment)
    object NetworkTools : Screen("network", "Network Tools", Icons.Default.Router)
    object Deployment : Screen("deploy", "Deployment", Icons.Default.FactCheck)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CdoApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Global State for the session
    val repairLogs = remember { mutableStateListOf<RepairLog>() }
    val deploymentTasks = remember { mutableStateListOf<DeploymentTask>() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color(0xFF001F54)) {
                DrawerHeader()
                Divider(Modifier.padding(vertical = 8.dp), color = Color.White.copy(0.1f))
                listOf(Screen.Dashboard, Screen.Troubleshooting, Screen.RepairLogs, Screen.NetworkTools, Screen.Deployment).forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        selected = false,
                        onClick = { navController.navigate(screen.route); scope.launch { drawerState.close() } },
                        icon = { Icon(screen.icon, null) },
                        colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = Color.White, unselectedIconColor = Color.Cyan)
                    )
                }
            }
        }
    ) {
        BlueGradientBackground {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White.copy(0.05f), titleContentColor = Color.White),
                        title = { Text("ICT FIELD OPS", fontWeight = FontWeight.Black) },
                        navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null, tint = Color.White) } }
                    )
                }
            ) { padding ->
                NavHost(navController, startDestination = Screen.Dashboard.route, modifier = Modifier.padding(padding)) {
                    composable(Screen.Dashboard.route) { DashboardContent(repairLogs.size, deploymentTasks.size) }
                    composable(Screen.Troubleshooting.route) { TroubleshootingScreen() }
                    composable(Screen.RepairLogs.route) { RepairLogScreen(repairLogs) }
                    composable(Screen.NetworkTools.route) { NetworkToolsScreen() }
                    composable(Screen.Deployment.route) { DeploymentScreen(deploymentTasks) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() { CdoApp() }
