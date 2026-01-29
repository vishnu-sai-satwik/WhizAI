package com.example.whizai

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow




class ProjectViewModel : ViewModel() {
    // FIRESTORE: Get a reference to the database and authentication services.
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // STATE: Create a private mutable state flow to hold the list of projects.
    // A StateFlow is a special type of state holder ideal for ViewModels.
    private val _projects = MutableStateFlow<List<Project>>(emptyList())

    // STATE: Expose an immutable version of the state flow for the UI to observe.
    val projects = _projects.asStateFlow()

    // --- For a single selected project on the ProjectDetailsScreen ---
    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject = _selectedProject.asStateFlow()

    // NEW: State for the list of tasks for the selected project.
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    // This `init` block is called automatically when the ViewModel is created.
    init {
        fetchProjects()
    }

    // FIRESTORE: This function fetches projects from Firestore in real-time.
    private fun fetchProjects() {
        val userId = auth.currentUser?.uid ?: return

        // We listen for real-time updates using addSnapshotListener.
        // This query gets all documents from the "projects" collection
        // where the "userId" field matches the currently logged-in user.
        db.collection("projects").whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ProjectViewModel", "Error fetching projects: ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    // When we get new data, we map it to our Project data class
                    // and update our state flow with the new list.
                    val uiProjects = snapshot.documents.mapNotNull { doc ->
                        val firestoreProject = doc.toObject<ProjectFirestoreModel>()
                        firestoreProject?.let {
                            Project(
                                id = doc.id,
                                title = it.name,
                                description = it.description,
                                color = mapCategoryToColor(it.category)
                            )
                        }
                    }
                    _projects.value = uiProjects
                }
            }
    }

    // ‼️TODO: I need to implement this function..!
    fun fetchProjectById(projectId: String) {
        // When we fetch a project, we should also fetch its tasks.
        fetchTasksForProject(projectId)

        db.collection("projects").document(projectId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ProjectViewModel", "Listen failed.", e)
                    _selectedProject.value = null
                    return@addSnapshotListener
                }
                snapshot?.let { it ->
                    val firestoreProject = it.toObject<ProjectFirestoreModel>()
                    firestoreProject?.let {
                        _selectedProject.value = Project(
                            id = snapshot.id,
                            title = it.name,
                            description = it.description,
                            color = mapCategoryToColor(it.category)
                        )
                    }
                }
            }
    }

    private fun fetchTasksForProject(projectId: String) {
        // This query gets all documents from the "tasks" collection
        // where the "projectId" field matches the ID of the current project.
        db.collection("tasks")
            .whereEqualTo("projectId", projectId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("ProjectViewModel", "Task listen failed.", e)
                    return@addSnapshotListener
                }
                snapshots?.let {
                    val taskList = it.documents.mapNotNull { doc ->
                        // We will need a Firestore model for tasks later.
                        // For now, let's assume a simple structure.
                        val title = doc.getString("title") ?: ""
                        val description = doc.getString("description") ?: ""
                        Task(
                            id = doc.id,
                            title = title,
                            description = description,
                            borderColor = Color(0xFFF87171), // Example color
                            iconColor = Color(0xFFFEE2E2)    // Example color
                        )
                    }
                    _tasks.value = taskList
                }
            }
    }

    private fun mapCategoryToColor(category: String): Color {
        return when (category) {
            "Work" -> Color(0xFF007AFF)
            "Personal" -> Color(0xFFFFA500)
            "Study" -> Color(0xFF228B22)
            "Health & Fitness" -> Color(0xFF8B0000)
            else -> Color.Gray
        }
    }
}

//                    val projectList = snapshot.documents.mapNotNull { it.toObject<Project>() }
//                    _projects.value = projectList


