package com.example.myapplication.core.domain.model

data class Question(
    val id: Int,
    val text: String,
    val used: Boolean = false
)
