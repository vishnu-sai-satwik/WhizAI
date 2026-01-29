# WhizAI - Intelligent Project Manager & Productivity Suite 🚀🧠
# Overview
WhizAI is an AI-integrated Android application designed to bridge the gap between project management and personal productivity. It combines task tracking with proven focus techniques (Pomodoro) and AI-driven motivation to help developers and students maintain peak efficiency during complex workflows.

# Project Status: 🚧 In Active Development (WIP)

# Focus: Productivity Optimization, Data Visualization, and Cloud Integration.

# ⚡ Key Technical Features
Generative AI Motivation Engine:

Integrates AI APIs to generate dynamic, context-aware motivational quotes and productivity tips, ensuring fresh content for users daily rather than static databases.

 Focus & Time Management Module:

Pomodoro Timer: Implemented using Kotlin Coroutines and Foreground Services to ensure accurate countdowns even when the app is in the background.

Designed to reduce cognitive load and prevent burnout during long coding sessions.

Data-Driven Analytics Dashboard:

Features detailed Progress Graphs & Statistics (utilizing charting libraries) to visualize work habits, completion rates, and focus streaks over time.

Provides actionable insights into user productivity patterns.

Hierarchical Task Management:

To-Do List Architecture: Supports main projects with nested sub-tasks, allowing users to break down monolithic goals into manageable chunks.

State management handled via Jetpack Compose for instant UI updates upon task completion.

Secure Cloud Authentication:

Integrated Firebase Authentication for secure, seamless user sign-up and login, ensuring user data privacy and cross-device potential.

# 🛠️ Tech Stack & Architecture
Language: Kotlin

UI Toolkit: Jetpack Compose (Material Design 3)

Backend/Auth: Firebase Authentication

Architecture: MVVM (Model-View-ViewModel)

Concurrency: Kotlin Coroutines (for Timer & API calls)

Data Visualization: Custom Charting / MPAndroidChart

Local Storage: Room Database (for offline Task persistence)
 
