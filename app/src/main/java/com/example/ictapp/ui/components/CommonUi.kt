package com.example.ictapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    var isChecked by remember { mutableStateOf(checked) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isChecked, onCheckedChange = { isChecked = it; onCheckedChange(it) }, colors = CheckboxDefaults.colors(checkedColor = Color.Cyan))
        Text(label, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun FieldInput(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(0.6f)) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Cyan, unfocusedBorderColor = Color.White, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
    )
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth().then(modifier), colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f)),
        shape = RoundedCornerShape(16.dp), content = { Column(Modifier.padding(16.dp), content = content) })
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(0.5f)); Text(value, color = Color.Cyan, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun QuickActionRow(icon: ImageVector, title: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.05f)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.Cyan); Spacer(Modifier.width(16.dp)); Text(title, color = Color.White)
    }
}

@Composable
fun BlueGradientBackground(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF000814), Color(0xFF001D3D), Color(0xFF003566))))) {
        CircuitBackground()
        content()
    }
}

@Composable
fun CircuitBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "circuit")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val color = Color.Cyan.copy(alpha = 0.05f * pulse)

        // Draw static circuit lines
        drawPath(
            path = Path().apply {
                moveTo(width * 0.1f, 0f)
                lineTo(width * 0.1f, height * 0.2f)
                lineTo(width * 0.3f, height * 0.3f)
                
                moveTo(width * 0.9f, height)
                lineTo(width * 0.9f, height * 0.8f)
                lineTo(width * 0.7f, height * 0.7f)
                
                moveTo(0f, height * 0.5f)
                lineTo(width * 0.2f, height * 0.5f)
                lineTo(width * 0.3f, height * 0.6f)
                
                moveTo(width, height * 0.4f)
                lineTo(width * 0.8f, height * 0.4f)
                lineTo(width * 0.7f, height * 0.3f)
            },
            color = color,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw circuit nodes
        drawCircle(color = color, radius = 6.dp.toPx(), center = Offset(width * 0.3f, height * 0.3f))
        drawCircle(color = color, radius = 6.dp.toPx(), center = Offset(width * 0.7f, height * 0.7f))
        drawCircle(color = color, radius = 6.dp.toPx(), center = Offset(width * 0.3f, height * 0.6f))
        drawCircle(color = color, radius = 6.dp.toPx(), center = Offset(width * 0.7f, height * 0.3f))
    }
}

@Composable
fun DrawerHeader() {
    Column(Modifier.padding(24.dp)) { Icon(Icons.Default.Terminal, null, tint = Color.Cyan, modifier = Modifier.size(40.dp)); Text("ICT OPS", fontWeight = FontWeight.Bold, color = Color.White) }
}
