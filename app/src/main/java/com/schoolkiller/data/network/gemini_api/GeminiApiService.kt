package com.schoolkiller.data.network.gemini_api


import com.schoolkiller.BuildConfig
import com.schoolkiller.data.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import javax.inject.Inject

class GeminiApiService @Inject constructor(
    private val client: HttpClient
) {

    private val generativeLanguageBaseUrl = "https://generativelanguage.googleapis.com"
    private val uploadHttp = "$generativeLanguageBaseUrl/upload"
    private val modelsHttp = "$generativeLanguageBaseUrl/v1beta/models"

    suspend fun uploadFileWithProgress(
        fileByteArray: ByteArray,
        fileName: String,
    ): Result<UploadModel> {
        return try {
            val response: HttpResponse = client.post(
                "${uploadHttp}/v1beta/files?key=${BuildConfig.gemini_api_key}"
            ) {
                header("X-Goog-Upload-Protocol", "resumable")
                header("X-Goog-Upload-Command", "start")
                header("X-Goog-Upload-Header-Content-Length", fileByteArray.size)
                header("X-Goog-Upload-Header-Content-Type", "image/jpeg")
                contentType(ContentType.Application.Json)
                setBody("{'file': {'display_name': '$fileName'}}")
            }

            val uploadUrl = response.headers["X-Goog-Upload-URL"]

            return if (uploadUrl != null) {
                Timber.d("Upload url is $uploadUrl")
                Result.success(UploadModel(uploadUrl))
            } else {
                Timber.d("Upload url is null")
                Result.failure(ServerResponseException(response, "Upload url is null"))
            }

        } catch (e: RedirectResponseException) {
            //3xx - responses
            Timber.d(e.message)
            Result.failure(e)
        } catch (e: ClientRequestException) {
            //4xx - response
            Timber.d(e.message)
            Result.failure(e)
        } catch (e: ServerResponseException) {
            //5xx - response
            Timber.d(e.message)
            Result.failure(e)
        } catch (e: Exception) {
            Timber.d(e.message)
            Result.failure(e)
        }
    }

    suspend fun uploadFileBytes(
        uploadUrl: String,
        fileByteArray: ByteArray,
    ): Result<String> {
        return try {
            val response: HttpResponse = client.post(uploadUrl) {
                header("Content-Length", fileByteArray.size)
                header("X-Goog-Upload-Offset", 0)
                header("X-Goog-Upload-Command", "upload, finalize")
                setBody(ByteReadChannel(fileByteArray))
            }

            val actualFileUri = Json.parseToJsonElement(response.bodyAsText())
                .jsonObject["file"]?.jsonObject?.get("uri")?.jsonPrimitive?.content

            if (!actualFileUri.isNullOrEmpty())
                Result.success(actualFileUri)
            else
                Result.failure(NullPointerException())

        } catch (e: RedirectResponseException) {
            //3xx - responses
            Timber.d(e.message)
            Result.failure(e)
        } catch (e: ClientRequestException) {
            //4xx - response
            Timber.d(e.message)
            Result.failure(e)
        } catch (e: ServerResponseException) {
            //5xx - response
            Timber.d(e.message)
            Result.failure(e)
        } catch (e: Exception) {
            Timber.d(e.message)
            Result.failure(e)
        }
    }

    suspend fun generateContent(requestBody: String): GeminiResponse<String> {

        return try {
            val response: HttpResponse = client.post(
                "${modelsHttp}/${Constants.GEMINI_FLASH_LATEST}?key=${BuildConfig.gemini_api_key}"

            ) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val plainTextResponse = jsonResponseToString(response.bodyAsText())
            // or just catch NullPointerException
            if (plainTextResponse.isNullOrEmpty())
                GeminiResponse.Error("Response is null or empty exception")
            else
                GeminiResponse.Success(plainTextResponse)

        } catch (e: RedirectResponseException) {
            //3xx - responses
            Timber.d(e.message)
            GeminiResponse.Error("Redirect exception", e.message)
        } catch (e: ClientRequestException) {
            //4xx - response
            Timber.d(e.message)
            GeminiResponse.Error("Client exception", e.message)
        } catch (e: ServerResponseException) {
            //5xx - response
            Timber.d(e.message)
            GeminiResponse.Error("Service is not available", e.message)
        } catch (e: Exception) {
            Timber.d(e.message)
            GeminiResponse.Error("Unknown error", e.message)
        }
    }

    private fun jsonResponseToString(jsonResponse: String): String? {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val geminiJsonResponse = json.decodeFromString<GeminiJsonResponse>(jsonResponse)
        // candidate.content.parts[2].text -> get second result,
        // but multiple candidates aren't supported yet (?)
        return geminiJsonResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
    }

}

@Serializable
data class UploadModel(
    val uploadUrl: String,
    val errorCode: Int? = null
)