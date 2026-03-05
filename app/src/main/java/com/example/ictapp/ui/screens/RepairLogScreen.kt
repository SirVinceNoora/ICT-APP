package com.example.ictapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ictapp.RepairLog
import com.example.ictapp.ui.components.FieldInput
import com.example.ictapp.ui.components.GlassCard
import com.example.ictapp.ui.theme.ICTAPPTheme

@Composable
fun RepairLogScreen(logs: MutableList<RepairLog>) {
    var issue by remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp)) {
        Text("Repair Tracker", color = Color.White, style = MaterialTheme.typography.headlineSmall)
        FieldInput(issue, { issue = it }, "What's the issue?")
        Button(onClick = {
            if(issue.isNotBlank()){
                logs.add(RepairLog(issue = issue, actionTaken = "Pending", date = System.currentTimeMillis(), location = "Site", deviceType = "PC", brand = "Generic", partsUsed = "", timeSpentMinutes = 0, status = "Open"))
                issue = ""
            }
        }, Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Text("Log Issue") }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(logs.asReversed()) { log ->
                GlassCard {
                    Text(log.issue, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Status: ${log.status}", color = Color.Cyan, fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview
@Composable
fun RepairLogScreenPreview() {
    val sampleLogs = remember {
        mutableListOf(
            RepairLog(issue = "PC won't turn on", actionTaken = "Pending", date = System.currentTimeMillis(), location = "Site", deviceType = "PC", brand = "Generic", partsUsed = "", timeSpentMinutes = 0, status = "Open"),
            RepairLog(issue = "Printer not working", actionTaken = "Pending", date = System.currentTimeMillis(), location = "Site", deviceType = "Printer", brand = "Generic", partsUsed = "", timeSpentMinutes = 0, status = "Open")
        )
    }
    ICTAPPTheme {
        RepairLogScreen(logs = sampleLogs)
    }
}
