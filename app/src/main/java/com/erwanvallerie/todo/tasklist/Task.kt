package com.erwanvallerie.todo.tasklist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.Serializable as s2

@Serializable
data class Task(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String = "No description",
    @SerialName("due_date")
    val due_date: String = "2021-01-01T00:00:00.000+00:00"
) : s2