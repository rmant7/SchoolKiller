package com.schoolkiller.data.network.gemini_api

object GeminiRequest {

    fun buildGeminiRequest(
        prompt: String,
        systemInstruction: String
    ): String {
        return """
            { 
                "system_instruction": {
                    "parts":
                        { "text": "$systemInstruction"}
                },
                "contents": [{
                    "parts": [
                        {"text":"$prompt"},
                    ]
                }]
            }                 
        """.trimIndent()
    }

    fun buildGeminiRequest(
        fileUri: String,
        prompt: String,
        systemInstruction: String
    ): String {
        val escapedFileUri = fileUri.replace("\"", "\\\"")
        return """
            { 
                "system_instruction": {
                    "parts":
                        { "text": "$systemInstruction"}
                },
                "contents": [{
                    "parts": [
                        {"text":"$prompt"},
                        {"file_data": {"mime_type": "image/jpeg", "file_uri": "$escapedFileUri"}}
                    ]
                }]
            }                 
        """.trimIndent()
    }

    fun buildGeminiRequest(
        uriList: List<String>,
        prompt: String,
        systemInstruction: String
    ): String {
        val requestBody = StringBuilder(
            """
                "system_instruction": {
                    "parts":
                        { "text": "$systemInstruction"}
                },
                "contents": [{
                    "parts": [
                        {"text":"$prompt"},
        """.trimIndent()
        )

        uriList.forEach { uri ->
            val escapedFileUri = uri.replace("\"", "\\\"")
            requestBody.append(
                """
                {"file_data": {"mime_type": "image/jpeg", "file_uri": "$escapedFileUri"}}
                """.trimIndent()
            )
        }

        requestBody.append(
            """
                    ]
                }]
            }
        """.trimIndent()
        )

        return requestBody.toString()
    }
}