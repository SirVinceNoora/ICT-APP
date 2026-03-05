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
    val technicianName: String = "V1NC3",
    val location: String,
    val deviceType: String,
    val brand: String,
    val modelNumber: String = "Generic",
    val serialNumber: String = "",
    val issue: String,
    val diagnosis: String = "",
    val actionTaken: String,
    val partsUsed: String,
    val costOfParts: Double = 0.0,
    val timeSpentMinutes: Int,
    val status: String,
    val customerSignature: String = ""
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
    val serialNumber: String = "",
    val assetTag: String = "",
    val date: Long,
    val location: String = "",
    val assignedTo: String = "",
    var isWindowsActivated: Boolean = false,
    var isOfficeInstalled: Boolean = false,
    var isDriversInstalled: Boolean = false,
    var isAntivirusInstalled: Boolean = false,
    var isNetworkJoined: Boolean = false,
    var isPrinterInstalled: Boolean = false,
    var isUpdatesDone: Boolean = false,
    var isUserCreated: Boolean = false,
    var isBackupConfigured: Boolean = false,
    var isEncryptionEnabled: Boolean = false,
    val notes: String = "",
    val status: String = "Pending"
)
