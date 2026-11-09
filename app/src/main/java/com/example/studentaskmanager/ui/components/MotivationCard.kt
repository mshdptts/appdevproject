package com.example.studentaskmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentaskmanager.ui.theme.Ink
import com.example.studentaskmanager.ui.theme.PaleBlue
import com.example.studentaskmanager.utils.summaryText

@Composable
fun MotivationCard(pending: Int, total: Int, name: String, modifier: Modifier = Modifier) {
    val message = remember(pending, total) { summaryText(pending, total, name) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Slightly rounder
        colors = CardDefaults.cardColors(containerColor = PaleBlue)
    ) {
        // INCREASED PADDING HERE
        Column(Modifier.padding(24.dp)) {
            Text(
                text = message,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                // INCREASED FONT SIZE
                fontSize = 18.sp,
                color = Ink
            )
            if (total > 0) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$pending pending, ${total - pending} done",
                    fontSize = 14.sp, // Bumped up from 12sp
                    color = Ink.copy(alpha = 0.7f)
                )
            }
        }
    }
}