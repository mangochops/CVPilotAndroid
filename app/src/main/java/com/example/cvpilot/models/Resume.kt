package com.example.cvpilot.models

import java.util.Date

data class Resume(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val name: String,
    val publicUrl: String,
    val createdAt: Date = Date()
)