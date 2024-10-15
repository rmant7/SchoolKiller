package com.schoolkiller.domain.usecases.api

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import javax.inject.Inject

class ExtractGeminiResponseUseCase @Inject constructor(

) {

    // @OptIn(ExperimentalSerializationApi::class)
    fun invoke(jsonResponse: String): String? {
        val json = Json {
            ignoreUnknownKeys = true
            //isLenient = true
            //explicitNulls = false
        }
        val geminiResponse = json.decodeFromString<GeminiResponse>(jsonResponse)
        // candidate.content.parts[2].text -> get second result,
        // but multiple candidates aren't supported yet (?)
        return geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
    }
}


@Serializable
data class GeminiResponse(
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