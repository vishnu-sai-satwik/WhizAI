package com.example.whizai

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


private const val FOCUS_TIME_SECONDS = 25 * 60
private const val SHORT_BREAK_SECONDS = 5 * 60
private const val LONG_BREAK_SECONDS = 15 * 60
private const val SESSIONS_UNTIL_LONG_BREAK  = 4

private val dummyTasks = listOf(
    Task("1","Firebase Integration", "Complete Firebase Integration for login form", Color(0xFFF87171), Color(0xFFFEE2E2)),
    Task("2","UI Polishing", "Fix alignment issues on the home screen", Color(0xFF34D399), Color(0xFFD1FAE5)),
    Task("3","API Documentation", "Write docs for the new user endpoint", Color(0xFF60A5FA), Color(0xFFDBEAFE))
)

//⏰
@Composable
fun PomodoroScreen() {
    var remainingTime by remember { mutableIntStateOf(FOCUS_TIME_SECONDS) }
    var totalTime by remember { mutableIntStateOf(FOCUS_TIME_SECONDS) }
    var isTimeRunning by remember { mutableStateOf(false) }
    var sessionType by remember { mutableStateOf("Focus") }
    var completeSessions by remember { mutableIntStateOf(0) }
    var currentTask by remember { mutableStateOf(dummyTasks.firstOrNull()) }
    var isTaskMenuExpanded by remember { mutableStateOf(false) }

    fun handleSessionEnd() {
        isTimeRunning = false
        if (sessionType == "Focus") {
            completeSessions++
            if (completeSessions % SESSIONS_UNTIL_LONG_BREAK == 0) {
                remainingTime = LONG_BREAK_SECONDS
                totalTime = LONG_BREAK_SECONDS
                sessionType = "Long Break"
            } else {
                remainingTime = SHORT_BREAK_SECONDS
                totalTime = SHORT_BREAK_SECONDS
                sessionType = "Short Break"
            }
        } else {
            remainingTime = FOCUS_TIME_SECONDS
            totalTime = FOCUS_TIME_SECONDS
            sessionType = "Focus"
        }
    }

    LaunchedEffect(key1 = isTimeRunning, key2 = remainingTime) {
        if (isTimeRunning && remainingTime > 0) {
            delay(1000L)
            remainingTime--
        } else if (isTimeRunning && remainingTime == 0) {
            handleSessionEnd()
        }
    }

    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }

    val progress by animateFloatAsState(
        targetValue = remainingTime.toFloat() / totalTime.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "Timer Progress Animation"
    )

    Scaffold (
        containerColor = Color(0xFFF5F7FA),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "Pomodoro Timer",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
//                Text(
//                    text = sessionType,
//                    fontWeight = FontWeight.Bold,
//                    style = MaterialTheme.typography.headlineSmall
//                )
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.size(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 12.dp,
                        strokeCap = StrokeCap.Round
                    )
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF7B61FF),
                        strokeWidth = 12.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = formatTime(remainingTime),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge
                    )

                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Session ${completeSessions + 1} of $SESSIONS_UNTIL_LONG_BREAK",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = currentTask?.title ?: "",
                        onValueChange = {
                            currentTask?.title = it
                        },
                        label = { Text("Focusing On") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                "Dropdown arrow"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isTaskMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isTaskMenuExpanded,
                        onDismissRequest = { isTaskMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        dummyTasks.forEach { task ->
                            DropdownMenuItem(
                                text = { Text(task.title) },
                                onClick = {
                                    currentTask = task
                                    isTaskMenuExpanded = false
                                    isTimeRunning = false
                                    remainingTime = FOCUS_TIME_SECONDS
                                    totalTime = FOCUS_TIME_SECONDS
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row (
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Button(
                        onClick = {
                            isTimeRunning = !isTimeRunning
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
                    ) {
                        Text(if (isTimeRunning) "Pause" else "Start")
                    }
                    Button(
                        onClick = {
                            isTimeRunning = false
                            remainingTime = FOCUS_TIME_SECONDS
                            totalTime = FOCUS_TIME_SECONDS
                            completeSessions = 0
                            sessionType = "Focus"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Reset")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PomodoroScreenPreview() {
    PomodoroScreen()
}