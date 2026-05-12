package com.example.cvpilot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Resume(
    val id: String? = null,

    @SerialName("user_id") // Maps Kotlin userId to DB user_id
    val userId: String,

    val title: String,

    @SerialName("file_url") // Maps Kotlin fileUrl to DB file_url
    val fileUrl: String?,

    val name: String = "", // Add this if you want to store the filename

    val content: String? = null // Your schema uses jsonb/text for content
)