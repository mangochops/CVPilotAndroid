package com.example.cvpilot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Resume(
    @SerialName("id")
    val id: String? = null,

    @SerialName("user_id")
    val userId: String,

    @SerialName("title")
    val title: String = "Primary Resume",

    @SerialName("name")
    val name: String,

    @SerialName("content")
    val content: JsonElement? = null, // Matches the jsonb type securely

    @SerialName("file_url")
    val fileUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)