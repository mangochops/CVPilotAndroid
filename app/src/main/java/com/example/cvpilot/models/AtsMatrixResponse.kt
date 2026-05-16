package com.example.cvpilot.models

import kotlinx.serialization.Serializable

@Serializable
data class AtsMatrixResponse(
    val overallScore: Int,
    val technicalOverlap: Float,
    val architectureOverlap: Float,
    val cloudOverlap: Float,
    val feedback: String
)