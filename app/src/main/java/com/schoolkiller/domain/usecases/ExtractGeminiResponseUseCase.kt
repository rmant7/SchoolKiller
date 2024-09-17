package com.schoolkiller.domain.usecases

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import javax.inject.Inject

class ExtractGeminiResponseUseCase @Inject constructor(

) {

    fun invoke(jsonResponse: String): String {
        val json = Json { ignoreUnknownKeys = true }
        val geminiResponse = json.decodeFromString<GeminiResponse>(jsonResponse)
        return geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "No content available"
    }
}


@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>
)

@Serializable
data class Candidate(
    val content: Content
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)