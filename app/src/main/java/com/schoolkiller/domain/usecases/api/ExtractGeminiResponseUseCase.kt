package com.schoolkiller.domain.usecases.api

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import javax.inject.Inject

class ExtractGeminiResponseUseCase @Inject constructor(

) {

    fun invoke(jsonResponse: String): String {
        val json = Json { ignoreUnknownKeys = true }
        val geminiResponse = json.decodeFromString<GeminiResponse>(jsonResponse)
        return geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "No content available"  // TODO { hardcode string }
    }
}


@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>
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
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)