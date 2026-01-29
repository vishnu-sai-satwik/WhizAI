package com.example.whizai

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

data class ProjectFirestoreModel(
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Composable
fun AddProjectScreen(navController: NavController) {
    var projectName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Category") }
    var isCategoryExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val categories = listOf("Work", "Personal", "Study", "Health & Fitness")
    val isFormValid = projectName.isNotBlank() && description.isNotBlank() && selectedCategory != "Category"

    val context = LocalContext.current
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    fun saveProject() {
        if (userId == null) {
            Toast.makeText(context, "You must be logged in to create a project.", Toast.LENGTH_SHORT).show()
            return
        }
        isLoading = true
        val project = ProjectFirestoreModel(
            userId = userId,
            name = projectName,
            description = description,
            category = selectedCategory
        )
        db.collection("projects")
            .add(project)
            .addOnSuccessListener {
                Toast.makeText(context, "Project created successfully!", Toast.LENGTH_SHORT).show()
                isLoading = false // Stop loading on success
                navController.popBackStack()


            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error creating project: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false // Stop loading on failure
            }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "Add New Project",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "WhizAI recommend you to add new project",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text("Name of your project") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            "Dropdown Icon",
                            Modifier.clickable { isCategoryExpanded = !isCategoryExpanded })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCategoryExpanded = !isCategoryExpanded },
                    shape = RoundedCornerShape(12.dp)
                )
                DropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                isCategoryExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { saveProject() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                enabled = isFormValid && !isLoading,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Proceed", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.intro_screen_pic_one),
                contentDescription = "Add Project Illustration",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddProjectScreenPreview() {
    AddProjectScreen(navController = rememberNavController())
}

