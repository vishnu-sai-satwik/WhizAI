package com.example.whizai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Data class for navigation items
data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

// This is the main screen shell that holds the bottom navigation.
@Composable
fun MainScreen(navController: NavController) {
    val bottomBarNavController = rememberNavController()

    val navItems = listOf(
        NavItem(Screen.MainScreen.route, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem(Screen.AddTasksScreen.route, "Add Task", Icons.Filled.Add, Icons.Outlined.Add),
        NavItem(Screen.PomodoroScreen.route, "Timer", Icons.Filled.Face, Icons.Outlined.Face),
        NavItem(Screen.ProjectDetailsScreen.route, "Project Details", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Handle FAB click */ },
                shape = CircleShape,
                containerColor = Color(0xFF7B61FF)
            ) {
                Icon(Icons.Filled.Add, "Add new item", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            WhizAIBottomNavBar(
                items = navItems,
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    bottomBarNavController.navigate(route) {
                        popUpTo(bottomBarNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // The main NavController is passed to the graph for navigating outside the bottom bar flow
            BottomNavGraph(mainNavController = navController, bottomNavController = bottomBarNavController)
        }
    }
}

@Composable
fun BottomNavGraph(mainNavController: NavController, bottomNavController: NavHostController) {
    val projectViewModel : ProjectViewModel = viewModel()
    NavHost(navController = bottomNavController, startDestination = Screen.MainScreen.route) {
        composable(Screen.MainScreen.route) { MainScreenContent(mainNavController, viewModel = projectViewModel) }
        composable(Screen.PomodoroScreen.route) { PomodoroScreen() }
        composable(Screen.AddTasksScreen.route) { AddTasksScreen(navController = mainNavController, projectName = "AI Dress Matcher") }

        composable(Screen.ProjectDetailsScreen.route) { ProjectDetailsScreen(navController = mainNavController, projectId = "preview_id", projectViewModel = projectViewModel) }
    }
}

@Composable
fun WhizAIBottomNavBar(
    items: List<NavItem>,
    currentRoute: String?,
    onItemSelected: (String) -> Unit
) {
    BottomAppBar(
        containerColor = Color.White.copy(alpha = 0.9f),
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            if (index == 2) {
                Spacer(modifier = Modifier.width(2.dp)) // Spacer for the FAB
            }
            NavigationBarItem(
                modifier = Modifier.weight(1f),
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

// FIRESTORE: The HomeScreenContent now accepts a ProjectViewModel.
@Composable
fun MainScreenContent(navController: NavController, viewModel: ProjectViewModel) {
    // FIRESTORE: Collect the projects from the ViewModel's state flow.
    // This will automatically update the UI whenever the data changes in Firestore.
    val projects by viewModel.projects.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DashboardHeader(userName = "Vishnu Sai Satwik", onNewProjectClick = { navController.navigate("add_project_screen") })
        Spacer(modifier = Modifier.height(10.dp))
        ProgressCard(progress = 0.2f, message = "You are in good speed, maintain that...")
        Spacer(modifier = Modifier.height(20.dp))
        // FIRESTORE: Pass the live list of projects from the ViewModel to the UI component.
        InProgressSection(
            projects = projects,
            onProjectClick = { projectId ->
                viewModel.fetchProjectById(projectId)
                navController.navigate("project_details_screen")
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        // This part remains the same, using dummy data for now.
        TodayTasksSection(
            tasks = listOf(
                Task(id = "1", title = "Task 1", description = "Description 1", borderColor = Color.Red, iconColor = Color.Blue),
                Task(id = "2", title = "Task 2", description = "Description 2", borderColor = Color.Green, iconColor = Color.Yellow)
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        AiSuggestionsCard(
            projectName = "AI Dress Matcher",
            suggestions = listOf(
                "Make the UI more clean & responsive",
                "Attract customers by showing some samples",
                "Make sure you integrated firebase to login functionality"
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        ContinueProjectFooter(onStartClick = { /* TODO */ })
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun DashboardHeader(userName: String, onNewProjectClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier.size(48.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Welcome Back,")
                Text(text = userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
        Button(
            onClick = onNewProjectClick,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Project Icon")
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "New Project", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProgressCard(progress: Float, message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF7B61FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = message, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "${(progress * 100).toInt()}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun InProgressCard(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = project.color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.description,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Project Icon",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun InProgressSection(projects: List<Project>, onProjectClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "In Progress",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E7FF)),
            ) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${projects.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(projects) { project ->
                InProgressCard(project = project, onClick = { onProjectClick(project.id) } )
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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
fun TodayTasksSection(tasks: List<Task>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Card(shape = CircleShape, colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E7FF))) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${tasks.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        tasks.forEach { task ->
            TaskCard(task = task)
        }
    }
}

@Composable
fun AiSuggestionsCard(projectName: String, suggestions: List<String>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI Suggestions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Card(shape = CircleShape, colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E7FF))) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${suggestions.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF60A5FA), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Project: $projectName",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                suggestions.forEach { suggestion ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", modifier = Modifier.padding(top = 4.dp))
                        Text(text = suggestion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ContinueProjectFooter(onStartClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Wanna continue your project?",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = onStartClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
        ) {
            Text("Start", fontWeight = FontWeight.Bold)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(navController = rememberNavController())
}

