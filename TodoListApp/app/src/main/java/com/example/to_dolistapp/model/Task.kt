package com.example.to_dolistapp.model

import java.time.LocalDate

data class Task(
    val name: String,
    val due: Long,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM
) {
    enum class Priority {
        LOW, MEDIUM, HIGH
    }
}