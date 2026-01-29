package com.example.whizai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTasksScreen(
    navController: NavController,
    projectName: String,

) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val tasks = remember { mutableStateListOf<Task>() }
    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = { Text("Add your Tasks", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (tasks.isNotEmpty()) {
                        IconButton(onClick = { /* Handle more actions */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                "WhizAI asks you to add your tasks for",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "\"$projectName\" project...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (tasks.isEmpty()) {
                EmptyTaskState()
            } else {
                AddedTasksList(modifier = Modifier.weight(1f), tasks = tasks)
            }
            Spacer(modifier = Modifier.height(32.dp))
            // CORRECTION: Added a dedicated button to open the bottom sheet for a better UX.
            if (tasks.isNotEmpty()) Spacer(modifier = Modifier.height(50.dp))
            Button(
                onClick = { showBottomSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (tasks.isEmpty()) Color(0xFFEF4444) else Color(0xFF007AFF)
                )
            ) {
                Text(
                    text = if (tasks.isEmpty()) "Add Your First Task" else "Add Another Task",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- Bottom Sheet for Adding Tasks ---
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                AddTaskSheetContent(
                    onAddTask = { title, description ->
                        tasks.add(
                            Task(
                                title = title,
                                description = description,
                                borderColor = Color(0xFFF87171),
                                iconColor = Color(0xFFF87171),
                                id = tasks.size.toString()
                            )
                        )
                    },
                    isAddingAnother = tasks.isNotEmpty()
                )
            }
        }
    }
}

@Composable
fun EmptyTaskState() {
    // IMPROVEMENT: Added weight modifier to take up available vertical space.
    Box(
        modifier = Modifier
            .fillMaxWidth(),
//            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "You\nHaven't\n" +
                    "added\nany\n" +
                    "Tasks!",
            fontSize = 54.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEF4444),
            textAlign = TextAlign.Center,
            lineHeight = 60.sp
        )
    }
}

@Composable
fun AddedTasksList(modifier: Modifier = Modifier, tasks: List<Task>) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) { task ->
            TaskCard(task = task)
        }
    }
}


@Composable
fun AddTaskSheetContent(onAddTask: (String, String) -> Unit, isAddingAnother: Boolean) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isFormValid = title.isNotBlank() && description.isNotBlank()

    Column(
        modifier = Modifier
            .padding(24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Add Tasks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { /* TODO: Show Date Picker */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Deadline", modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Deadline")
            }
            OutlinedButton(
                onClick = { /* TODO: Show Icon Picker */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Select Icons")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onAddTask(title, description)
                title = ""
                description = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAddingAnother) Color(0xFF007AFF) else Color(0xFFEF4444)
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(if (isAddingAnother) "Add Another Task" else "Add Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddTasksScreenPreview() {
    AddTasksScreen(navController = rememberNavController(), projectName = "AI Dress Matcher")
}

