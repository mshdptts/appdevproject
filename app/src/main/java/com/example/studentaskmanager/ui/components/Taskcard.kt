package com.example.studentaskmanager.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentaskmanager.data.Task
import com.example.studentaskmanager.ui.theme.Ink
import com.example.studentaskmanager.ui.theme.Navy
import com.example.studentaskmanager.ui.theme.PaleBlue
import com.example.studentaskmanager.utils.dueInfo

@Composable
fun TaskCard(
    task: Task,
    onMarkComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val due = dueInfo(task.dueDate)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Ink.copy(alpha = 0.15f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (task.completed) Color(0xFFDDEBDD) else PaleBlue,
                    modifier = Modifier.clickable {
                        if (!task.completed) onMarkComplete()
                    }
                ) {
                    Text(
                        text = if (task.completed) "COMPLETED" else "MARK COMPLETE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (task.completed) Color(0xFF1E6B2E) else Navy,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = task.description,
                    fontSize = 13.sp,
                    color = Ink.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriorityPill(task.priority)

                if (due != null && !task.completed) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Due date",
                            tint = if (due.overdue) Color(0xFFB3261E) else Navy.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = due.text,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (due.overdue) Color(0xFFB3261E) else Navy.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}