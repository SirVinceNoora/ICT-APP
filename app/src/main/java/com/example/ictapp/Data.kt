package com.example.ictapp

data class TroubleshootingEntry(
    val id: Int = 0,
    val title: String,
    val category: String,
    val brand: String,
    val symptoms: String,
    val causes: String,
    val solution: String,
    val estimatedFixTime: String,
    val notes: String
)

data class RepairLog(
    val id: Int = 0,
    val date: Long,
    val location: String,
    val deviceType: String,
    val brand: String,
    val issue: String,
    val actionTaken: String,
    val partsUsed: String,
    val timeSpentMinutes: Int,
    val status: String
)

data class SavedIp(
    val id: Int = 0,
    val deviceName: String,
    val ipAddress: String,
    val notes: String
)

data class DeploymentTask(
    val id: Int = 0,
    val unitName: String,
    val date: Long,
    val isWindowsActivated: Boolean = false,
    val isOfficeInstalled: Boolean = false,
    val isDriversInstalled: Boolean = false,
    val isAntivirusInstalled: Boolean = false,
    val isNetworkJoined: Boolean = false,
    val isPrinterInstalled: Boolean = false,
    val isUpdatesDone: Boolean = false,
    val isUserCreated: Boolean = false,
    val notes: String = ""
)
