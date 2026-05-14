package com.example.cvpilot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoverLetter(
    val id: String? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("company_name")
    val companyName: String? = "Unknown Company",
    @SerialName("job_title")
    val jobTitle: String? = "Position",
    val content: String,
    @SerialName("created_at")
    val createdAt: String? = null
)

