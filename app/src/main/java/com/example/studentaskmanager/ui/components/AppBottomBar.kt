package com.example.studentaskmanager.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentaskmanager.ui.theme.MutedIcon
import com.example.studentaskmanager.ui.theme.Navy
import com.example.studentaskmanager.ui.theme.PaleBlue

@Composable
fun AppBottomBar(selected: String, onSelect: (String) -> Unit, onAdd: () -> Unit) {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Navy,
        selectedTextColor = Navy,
        indicatorColor = PaleBlue,
        unselectedIconColor = MutedIcon,
        unselectedTextColor = MutedIcon
    )

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = selected == "home", onClick = { onSelect("home") }, colors = colors,
            icon = {
                Icon(
                    imageVector = if (selected == "home") Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("HOME", fontSize = 11.sp) }
        )

        NavigationBarItem(
            selected = selected == "tasks", onClick = { onSelect("tasks") }, colors = colors,
            icon = {
                // Uses a nice checklist icon instead of a calendar
                Icon(
                    imageVector = if (selected == "tasks") Icons.Filled.Checklist else Icons.Outlined.Checklist,
                    contentDescription = "Tasks"
                )
            },
            label = { Text("TASKS", fontSize = 11.sp) }
        )

        // The Center Add Button
        NavigationBarItem(
            selected = false, onClick = onAdd, colors = colors,
            icon = {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Add Task",
                    modifier = androidx.compose.ui.Modifier.size(32.dp) // Make the + bigger!
                )
            },
            label = { Text("ADD", fontSize = 11.sp) }
        )

        NavigationBarItem(
            selected = selected == "progress", onClick = { onSelect("progress") }, colors = colors,
            icon = {
                // Uses a bar chart icon for progress
                Icon(
                    imageVector = if (selected == "progress") Icons.Filled.BarChart else Icons.Outlined.BarChart,
                    contentDescription = "Progress"
                )
            },
            label = { Text("PROGRESS", fontSize = 11.sp) }
        )

        NavigationBarItem(
            selected = selected == "profile", onClick = { onSelect("profile") }, colors = colors,
            icon = {
                Icon(
                    imageVector = if (selected == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("PROFILE", fontSize = 11.sp) }
        )
    }
}