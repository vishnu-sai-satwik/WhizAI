package com.example.whizai

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        composable(Screen.SplashScreen.route) { SplashScreen(navController = navController) }
        composable(Screen.LoginCredentialsScreen.route) { LoginCredentialsScreen(navController = navController) }
        composable(Screen.SignupScreen.route) { SignupScreen(navController = navController) }
        composable(Screen.MainScreen.route) { MainScreen(navController = navController) }
        composable(Screen.AddProjectScreen.route) { AddProjectScreen(navController = navController) }
        composable(
            route = "project_details_screen/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            if (projectId != null) {
                ProjectDetailsScreen(
                    navController = navController,
                    projectId = projectId,
                    projectViewModel = viewModel()
                )
            }

        }
        composable(Screen.ProjectDetailsScreen.route) { ProjectDetailsScreen(navController = navController, projectId = "preview_id", projectViewModel = viewModel()) }
        composable(Screen.AddTasksScreen.route) { AddTasksScreen(navController = navController, projectName = "AI Dress Matcher") }
        composable(Screen.PomodoroScreen.route) { PomodoroScreen() }


    }
}