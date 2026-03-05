package com.example.ictapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ictapp.RepairLog
import com.example.ictapp.ui.components.DetailRow
import com.example.ictapp.ui.components.FieldInput
import com.example.ictapp.ui.components.GlassCard
import com.example.ictapp.ui.theme.ICTAPPTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairLogScreen(logs: MutableList<RepairLog>, onBack: () -> Unit = {}) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedLogForEdit by remember { mutableStateOf<RepairLog?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Repair Tracker", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Color.Cyan) {
                    Icon(Icons.Default.Add, contentDescription = "Add Log", tint = Color.Black)
                }
            }
        ) { padding ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                if (logs.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.AutoMirrored.Filled.Assignment, null, tint = Color.White.copy(0.2f), modifier = Modifier.size(64.dp))
                            Text("No repair history found", color = Color.White.copy(0.5f))
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(logs) { log ->
                            RepairLogCard(log, onEdit = { selectedLogForEdit = log })
                        }
                    }
                }
            }
        }

        // Blurry overlay when dialog is shown
        if (showAddDialog || selectedLogForEdit != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .blur(12.dp)
            )
        }

        if (showAddDialog) {
            RepairEntryDialog(
                onDismiss = { showAddDialog = false },
                onSave = { newLog ->
                    logs.add(newLog)
                    showAddDialog = false
                }
            )
        }

        if (selectedLogForEdit != null) {
            RepairEntryDialog(
                log = selectedLogForEdit,
                onDismiss = { selectedLogForEdit = null },
                onSave = { updatedLog ->
                    val index = logs.indexOfFirst { it.id == updatedLog.id }
                    if (index != -1) {
                        logs[index] = updatedLog
                    } else {
                        // Fallback for current session reference
                        val refIndex = logs.indexOf(selectedLogForEdit)
                        if (refIndex != -1) logs[refIndex] = updatedLog
                    }
                    selectedLogForEdit = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairEntryDialog(
    log: RepairLog? = null,
    onDismiss: () -> Unit,
    onSave: (RepairLog) -> Unit
) {
    var deviceType by remember { mutableStateOf(log?.deviceType ?: "") }
    var brand by remember { mutableStateOf(log?.brand ?: "") }
    var issue by remember { mutableStateOf(log?.issue ?: "") }
    var diagnosis by remember { mutableStateOf(log?.diagnosis ?: "") }
    var actionTaken by remember { mutableStateOf(log?.actionTaken ?: "") }
    var status by remember { mutableStateOf(log?.status ?: "In Progress") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))))
                .padding(1.dp)
                .background(Color(0xFF001F54).copy(alpha = 0.9f), RoundedCornerShape(28.dp))
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    text = if (log == null) "NEW REPAIR ENTRY" else "UPDATE REPAIR",
                    color = Color.Cyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(20.dp))
                
                FieldInput(deviceType, { deviceType = it }, "Device Type")
                Spacer(Modifier.height(8.dp))
                FieldInput(brand, { brand = it }, "Brand")
                Spacer(Modifier.height(8.dp))
                FieldInput(issue, { issue = it }, "Issue")
                Spacer(Modifier.height(8.dp))
                FieldInput(diagnosis, { diagnosis = it }, "Diagnosis")
                Spacer(Modifier.height(8.dp))
                FieldInput(actionTaken, { actionTaken = it }, "Action Taken")
                
                Spacer(Modifier.height(16.dp))
                Text("STATUS", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("In Progress", "Completed", "Pending").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Cyan,
                                selectedLabelColor = Color.Black,
                                containerColor = Color.White.copy(0.05f),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("CANCEL", color = Color.White.copy(0.6f)) }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onSave(log?.copy(
                                deviceType = deviceType,
                                brand = brand,
                                issue = issue,
                                diagnosis = diagnosis,
                                actionTaken = actionTaken,
                                status = status
                            ) ?: RepairLog(
                                id = Random().nextInt(),
                                date = System.currentTimeMillis(),
                                location = "On-site",
                                deviceType = deviceType,
                                brand = brand,
                                issue = issue,
                                diagnosis = diagnosis,
                                actionTaken = actionTaken,
                                partsUsed = "None",
                                timeSpentMinutes = 0,
                                status = status
                            ))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SAVE RECORD", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RepairLogCard(log: RepairLog, onEdit: () -> Unit) {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    GlassCard {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(log.deviceType, color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(sdf.format(Date(log.date)), color = Color.White.copy(0.5f), fontSize = 11.sp)
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, null, tint = Color.Cyan.copy(0.7f), modifier = Modifier.size(16.dp))
                    }
                }
            }
            Text("${log.brand} - ${log.modelNumber}", color = Color.White, fontSize = 14.sp)
            
            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color.White.copy(0.1f))
            
            DetailRow("Issue", log.issue)
            if (log.diagnosis.isNotEmpty()) DetailRow("Diagnosis", log.diagnosis)
            
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = log.status.uppercase(), 
                    color = when(log.status) {
                        "Completed" -> Color.Green
                        "Pending" -> Color.Red
                        else -> Color.Yellow
                    }, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 11.sp
                )
                Text("${log.timeSpentMinutes} MINS", color = Color.White.copy(0.6f), fontSize = 11.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RepairLogScreenPreview() {
    val sampleLogs = remember {
        mutableStateListOf(
            RepairLog(
                date = System.currentTimeMillis(),
                location = "Office A",
                deviceType = "Laptop",
                brand = "Dell",
                issue = "No Power",
                diagnosis = "Testing...",
                actionTaken = "None",
                partsUsed = "None",
                timeSpentMinutes = 10,
                status = "In Progress"
            )
        )
    }
    ICTAPPTheme {
        Box(Modifier.background(Color(0xFF001F54))) {
            RepairLogScreen(logs = sampleLogs)
        }
    }
}
