package com.example.whizai

/**
 * A sealed class to define the navigation routes in the app.
 * Using a sealed class ensures type safety and prevents typos when navigating.
 *
 * "@property route" The string path for the navigation route.
 */

sealed class Screen(val route: String)  {
    object SplashScreen : Screen("splash_screen")
    object LoginCredentialsScreen : Screen("login_screen")
    object SignupScreen : Screen("signup_screen")
    object MainScreen : Screen("main_screen")
    object AddProjectScreen : Screen("add_project_screen")
    object ProjectDetailsScreen : Screen("project_details_screen")
    object AddTasksScreen : Screen("add_tasks_screen")
    object PomodoroScreen : Screen("pomodoro_screen")


}