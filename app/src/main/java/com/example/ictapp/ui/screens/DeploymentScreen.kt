package com.example.ictapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ictapp.DeploymentTask
import com.example.ictapp.ui.components.CheckRow
import com.example.ictapp.ui.components.GlassCard
import com.example.ictapp.ui.theme.ICTAPPTheme

@Composable
fun DeploymentScreen(tasks: MutableList<DeploymentTask>) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Active Deployments", color = Color.White, style = MaterialTheme.typography.headlineSmall) }
        items(tasks) { task ->
            var expanded by remember { mutableStateOf(false) }
            GlassCard(Modifier.clickable { expanded = !expanded }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Computer, null, tint = if (task.isNetworkJoined) Color.Green else Color.Cyan)
                    Spacer(Modifier.width(12.dp))
                    Text(task.unitName, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                    Icon(if(expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color.White)
                }
                if (expanded) {
                    Spacer(Modifier.height(12.dp))
                    // These now work because Data.kt uses 'var'
                    CheckRow("Windows Active", task.isWindowsActivated) { task.isWindowsActivated = it }
                    CheckRow("Drivers Loaded", task.isDriversInstalled) { task.isDriversInstalled = it }
                    CheckRow("Domain Joined", task.isNetworkJoined) { task.isNetworkJoined = it }
                }
            }
        }
        item {
            Button(onClick = { tasks.add(DeploymentTask(unitName = "Station-${tasks.size+1}", date = System.currentTimeMillis())) }, modifier = Modifier.fillMaxWidth()) {
                Text("Add New Unit")
            }
        }
    }
}

@Preview
@Composable
fun DeploymentScreenPreview() {
    val sampleTasks = remember {
        mutableListOf(
            DeploymentTask(
                unitName = "Station-1",
                date = System.currentTimeMillis(),
                isWindowsActivated = true,
                isDriversInstalled = true,
                isNetworkJoined = true
            ),
            DeploymentTask(
                unitName = "Station-2",
                date = System.currentTimeMillis(),
                isWindowsActivated = true,
                isDriversInstalled = false,
                isNetworkJoined = false
            ),
            DeploymentTask(
                unitName = "Station-3",
                date = System.currentTimeMillis(),
                isWindowsActivated = false,
                isDriversInstalled = false,
                isNetworkJoined = false
            )
        )
    }
    ICTAPPTheme {
        DeploymentScreen(tasks = sampleTasks)
    }
}
