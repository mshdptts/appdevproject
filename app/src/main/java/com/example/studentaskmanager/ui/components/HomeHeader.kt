package com.example.studentaskmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentaskmanager.ui.theme.Cream
import com.example.studentaskmanager.ui.theme.DustyBlue
import com.example.studentaskmanager.ui.theme.Navy
import com.example.studentaskmanager.utils.greeting

@Composable
fun HomeHeader(name: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Navy)
            .statusBarsPadding()
            // INCREASED PADDING HERE
            .padding(horizontal = 28.dp, vertical = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Hi $name",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                // INCREASED FONT SIZE
                fontSize = 28.sp,
                color = Cream,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = greeting(),
                fontSize = 15.sp,
                color = Cream.copy(alpha = 0.85f)
            )
        }

        Spacer(Modifier.width(16.dp))

        // BIGGER AVATAR
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(DustyBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}