package com.schoolkiller.data_Layer.network.api

import com.schoolkiller.BuildConfig
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
import timber.log.Timber
import javax.inject.Inject

class GeminiApiService @Inject constructor(
    private val client: HttpClient
) {

    suspend fun uploadFileWithProgress(
        fileByteArray: ByteArray,
        fileName: String,
    ): Result<UploadModel> {
        return try {
            val response: HttpResponse = client.post(
                "${HttpRoutes.UPLOAD}/v1beta/files?key=${BuildConfig.gemini_api_key}"
            ) {
                header("X-Goog-Upload-Protocol", "resumable")
                header("X-Goog-Upload-Command", "start")
                header("X-Goog-Upload-Header-Content-Length", fileByteArray.size)
                header("X-Goog-Upload-Header-Content-Type", "image/jpeg")
                contentType(ContentType.Application.Json)
                setBody("{'file': {'display_name': '$fileName'}}")
            }

            val uploadUrl =
                response.headers["X-Goog-Upload-URL"] ?: throw Exception("Upload URL not found")
            Result.success(UploadModel(uploadUrl))
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

            val fileUri = response.bodyAsText()
            Result.success(fileUri)
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

    suspend fun generateContent(fileUri: String, prompt: String): GeminiResponse<String> {

        val escapedFileUri = fileUri.replace("\"", "\\\"")

        return try {
            val response: HttpResponse = client.post(
                "${HttpRoutes.MODELS}/gemini-1.5-flash:generateContent?key=${BuildConfig.gemini_api_key}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(
                    """
            {
                "contents": [{
                    "parts":[
                        {"text": "$prompt"},
                        {"file_data": {"mime_type": "image/jpeg", "file_uri": "$escapedFileUri"}}
                    ]
                }]
            }
        """.trimIndent()
                )
            }
            GeminiResponse.Success(response.bodyAsText())

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

}

@Serializable
data class UploadModel(val uploadUrl: String)