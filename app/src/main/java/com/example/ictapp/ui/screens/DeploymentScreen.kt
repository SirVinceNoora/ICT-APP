package com.example.ictapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ictapp.DeploymentTask
import com.example.ictapp.ui.components.CheckRow
import com.example.ictapp.ui.components.DetailRow
import com.example.ictapp.ui.components.FieldInput
import com.example.ictapp.ui.components.GlassCard
import com.example.ictapp.ui.theme.ICTAPPTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeploymentScreen(
    tasks: List<DeploymentTask>, 
    onBack: () -> Unit = {},
    onSaveTask: (DeploymentTask) -> Unit = {},
    onUpdateTask: (DeploymentTask) -> Unit = {}
) {
    var selectedTaskForEdit by remember { mutableStateOf<DeploymentTask?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Active Deployments", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }
            items(tasks) { task ->
                DeploymentCard(
                    task = task, 
                    onEditClick = { selectedTaskForEdit = task },
                    onTaskUpdate = onUpdateTask
                )
            }
            item {
                Button(
                    onClick = { 
                        onSaveTask(DeploymentTask(
                            unitName = "WORKSTATION-${tasks.size + 1}", 
                            date = System.currentTimeMillis(),
                            status = "In Progress"
                        )) 
                    }, 
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Provision New Unit", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedTaskForEdit != null) {
            EditDeploymentDialog(
                task = selectedTaskForEdit!!,
                onDismiss = { selectedTaskForEdit = null },
                onSave = { updatedTask ->
                    onUpdateTask(updatedTask)
                    selectedTaskForEdit = null
                }
            )
        }
    }
}

@Composable
fun DeploymentCard(task: DeploymentTask, onEditClick: () -> Unit, onTaskUpdate: (DeploymentTask) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    GlassCard(Modifier.clickable { expanded = !expanded }) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).background(
                        if (task.status == "Completed") Color.Green.copy(0.1f) else Color.Cyan.copy(0.1f), 
                        RoundedCornerShape(8.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Computer, 
                        null, 
                        tint = if (task.status == "Completed") Color.Green else Color.Cyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(task.unitName, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
                    Text("Asset: ${task.assetTag.ifEmpty { "Pending Tag" }}", color = Color.White.copy(0.5f), fontSize = 11.sp)
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, "Edit", tint = Color.Cyan, modifier = Modifier.size(20.dp))
                }
                Icon(if(expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color.White.copy(0.5f))
            }
            
            if (expanded) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(0.1f))
                Spacer(Modifier.height(12.dp))
                
                DetailRow("Serial Number", task.serialNumber.ifEmpty { "N/A" })
                DetailRow("Location", task.location.ifEmpty { "Unassigned" })
                DetailRow("Assigned To", task.assignedTo.ifEmpty { "TBD" })
                DetailRow("Provision Date", sdf.format(Date(task.date)))
                
                Spacer(Modifier.height(16.dp))
                Text("PROVISIONING CHECKLIST", color = Color.Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                
                CheckRow("Windows Activated", task.isWindowsActivated) { onTaskUpdate(task.copy(isWindowsActivated = it)) }
                CheckRow("Drivers Installed", task.isDriversInstalled) { onTaskUpdate(task.copy(isDriversInstalled = it)) }
                CheckRow("Office Suite", task.isOfficeInstalled) { onTaskUpdate(task.copy(isOfficeInstalled = it)) }
                CheckRow("Security (Antivirus)", task.isAntivirusInstalled) { onTaskUpdate(task.copy(isAntivirusInstalled = it)) }
                CheckRow("Network/Domain Join", task.isNetworkJoined) { onTaskUpdate(task.copy(isNetworkJoined = it)) }
                CheckRow("Backup Configured", task.isBackupConfigured) { onTaskUpdate(task.copy(isBackupConfigured = it)) }
                
                if (task.notes.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Notes: ${task.notes}", color = Color.White.copy(0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EditDeploymentDialog(task: DeploymentTask, onDismiss: () -> Unit, onSave: (DeploymentTask) -> Unit) {
    var unitName by remember { mutableStateOf(task.unitName) }
    var assignedTo by remember { mutableStateOf(task.assignedTo) }
    var assetTag by remember { mutableStateOf(task.assetTag) }
    var status by remember { mutableStateOf(task.status) }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(8.dp)) {
                Text("Edit Deployment", color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))
                FieldInput(unitName, { unitName = it }, "Workstation Name")
                Spacer(Modifier.height(8.dp))
                FieldInput(assignedTo, { assignedTo = it }, "Assigned Technician/User")
                Spacer(Modifier.height(8.dp))
                FieldInput(assetTag, { assetTag = it }, "Asset Tag")
                Spacer(Modifier.height(8.dp))
                
                Text("Status", color = Color.White.copy(0.6f), fontSize = 12.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Pending", "In Progress", "Completed").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Cyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color.White.copy(0.6f)) }
                    Button(
                        onClick = { onSave(task.copy(unitName = unitName, assignedTo = assignedTo, assetTag = assetTag, status = status)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black)
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeploymentScreenPreview() {
    ICTAPPTheme {
        Box(Modifier.background(Color(0xFF001F54))) {
            DeploymentScreen(tasks = emptyList())
        }
    }
}
