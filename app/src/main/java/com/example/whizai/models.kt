package com.example.whizai

import androidx.compose.ui.graphics.Color

data class Project(
    val id: String,
    val title: String,
    val description: String,
    val color: Color
)

data class Task(
    val id: String,
    var title: String,
    val description: String,
    val borderColor: Color,
    val iconColor: Color
)

data class TaskFirestoreModel(
    val userId: String = "",
    val projectId: String = "", // Links the task to a project
    val title: String = "",
    val description: String = "",
    val status: String = "todo", // e.g., "todo", "in_progress", "done"
    val createdAt: Long = System.currentTimeMillis()
)