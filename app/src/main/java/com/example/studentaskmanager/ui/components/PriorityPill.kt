package com.example.studentaskmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriorityPill(priority: String) {
    val normalizedPriority = priority.trim().uppercase()
    val (bg, fg) = when (normalizedPriority) {
        "URGENT" -> Color(0xFFFDE0DE) to Color(0xFFB3261E)
        "MEDIUM" -> Color(0xFFFFF0CC) to Color(0xFF7A4F00)
        else -> Color(0xFFDDEBDD) to Color(0xFF1E6B2E)
    }
    Text(
        text = "PRIORITY: $normalizedPriority",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = fg,
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}