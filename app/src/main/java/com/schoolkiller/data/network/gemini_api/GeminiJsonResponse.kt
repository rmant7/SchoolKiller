package com.schoolkiller.data.network.gemini_api

import kotlinx.serialization.Serializable

@Serializable
data class GeminiJsonResponse(
    val candidates: List<Candidate?>? = null
)

/**
 * Default value of content was assigned to null
 * to prevent missing field error
 */
@Serializable
data class Candidate(
    val content: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>? = null
)

@Serializable
data class Part(
    val text: String? = null
)