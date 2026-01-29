package com.example.whizai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    navController: NavController,
    projectId: String,
    projectViewModel: ProjectViewModel // CORRECTION: The screen now accepts the shared ViewModel.
) {
    // It no longer creates its own ViewModel. It uses the one passed from the navigation graph.
    val project by projectViewModel.selectedProject.collectAsState()
    val tasks by projectViewModel.tasks.collectAsState() // Observe the tasks state.

    LaunchedEffect(key1 = projectId) {
        projectViewModel.fetchProjectById(projectId)
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = { Text("Project Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle more actions */ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More",
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (project == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                val currentProject = project!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ProjectOverviewSection(
                        projectName = currentProject.title,
                        description = currentProject.description,
                        categories = listOf("Work", "Study") // Placeholder
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ProjectProgressSection(progress = 0.6f) // Placeholder
                    Spacer(modifier = Modifier.height(24.dp))
                    // CORRECTION: The ProjectTasksSection now displays the live task data.
                    ProjectTasksSection(
                        navController = navController,
                        projectId = currentProject.id,
                        tasks = tasks
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    DeleteProjectButton(onClick = { /* TODO */ })
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectOverviewSection(projectName: String, description: String, categories: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = projectName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Project Description:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0))
        ) {
            Text(
                text = description,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Category:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                AssistChip(
                    onClick = { /* TODO */ },
                    label = { Text(category) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

@Composable
fun ProjectProgressSection(progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Progress:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ProjectTasksSection(tasks: List<Task>, navController: NavController, projectId: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Tasks:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Button(
                onClick = { navController.navigate("add_tasks_screen") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Add Task")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Display an empty state message if there are no tasks.
        if (tasks.isEmpty()) {
            Text(
                text = "No tasks yet. Click '+ Add Task' to get started!",
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                tasks.forEach { task ->
                    TaskCard1(task = task)
                }
            }
        }
    }
}

@Composable
fun TaskCard1(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, task.borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Task Completed",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = task.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Task",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(task.iconColor)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DeleteProjectButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEF4444),
            contentColor = Color.White
        )
    ) {
        Text("Delete Project", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun ProjectDetailsScreenPreview() {
    // We need a dummy ViewModel for the preview to work.
    ProjectDetailsScreen(
        navController = rememberNavController(),
        projectId = "preview_id",
        projectViewModel = viewModel() // This will use a default, empty ViewModel for the preview.
    )
}

