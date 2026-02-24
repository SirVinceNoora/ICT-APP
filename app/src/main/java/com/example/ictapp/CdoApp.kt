package com.example.ictapp

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

// --- Themes ---
private val DeepBlue = Color(0xFF0D47A1)
private val TealAccent = Color(0xFF00BFA5)

// --- Navigation ---
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Troubleshooting : Screen("troubleshooting", "Knowledge Base", Icons.Default.Handyman)
    object RepairLogs : Screen("repair_logs", "Repair Logs", Icons.Default.Assignment)
    object NetworkTools : Screen("network_tools", "Network Tools", Icons.Default.Router)
    object Deployment : Screen("deployment", "Deployment", Icons.Default.FactCheck)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.BarChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CdoApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DeepBlue,
                drawerContentColor = Color.White
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "ICT Ops Console",
                    modifier = Modifier.padding(24.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                val screens = listOf(
                    Screen.Dashboard, Screen.Troubleshooting, Screen.RepairLogs,
                    Screen.NetworkTools, Screen.Deployment, Screen.Analytics
                )
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(screen.icon, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = TealAccent,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(screens().find { it.route == currentRoute }?.title ?: "ICT Ops Console") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Toggle Light/Dark */ }) {
                            Icon(Icons.Default.Brightness4, contentDescription = "Theme")
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(navController, startDestination = Screen.Dashboard.route, modifier = Modifier.padding(padding)) {
                composable(Screen.Dashboard.route) { DashboardContent() }
                composable(Screen.Troubleshooting.route) { TroubleshootingContent() }
                composable(Screen.RepairLogs.route) { RepairLogsContent() }
                composable(Screen.NetworkTools.route) { NetworkToolsContent() }
                composable(Screen.Deployment.route) { DeploymentContent() }
                composable(Screen.Analytics.route) { AnalyticsContent() }
            }
        }
    }
}

fun screens() = listOf(Screen.Dashboard, Screen.Troubleshooting, Screen.RepairLogs, Screen.NetworkTools, Screen.Deployment, Screen.Analytics)

@Composable
fun DashboardContent() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Overview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Knowledge", "24", Icons.Default.MenuBook, Modifier.weight(1f))
                StatCard("Logs", "156", Icons.Default.History, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Deploy", "12", Icons.Default.RocketLaunch, Modifier.weight(1f))
                StatCard("Tools", "8", Icons.Default.Construction, Modifier.weight(1f))
            }
        }
        item {
            Spacer(Modifier.height(24.dp))
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DeepBlue), shape = RoundedCornerShape(8.dp)) {
                Text("New Repair Log")
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = DeepBlue)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

// --- Features ---
@Composable fun TroubleshootingContent() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = "", onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search Knowledge Base...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Categories", fontWeight = FontWeight.Bold)
        // ... list of categories
    }
}

@Composable fun RepairLogsContent() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Personal Repair Log System") } }

@Composable fun NetworkToolsContent() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("IP Calculator", style = MaterialTheme.typography.titleLarge)
        // Simple UI for IP Calc
    }
}

@Composable fun DeploymentContent() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Deployment Checklist") } }
@Composable fun AnalyticsContent() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Analytics Charts") } }
