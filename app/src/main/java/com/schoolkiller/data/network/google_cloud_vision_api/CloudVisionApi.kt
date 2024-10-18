package com.schoolkiller.data.network.google_cloud_vision_api

import android.graphics.Bitmap
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.AnnotateImageRequest
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse
import com.google.api.services.vision.v1.model.EntityAnnotation
import com.google.api.services.vision.v1.model.Feature
import com.google.api.services.vision.v1.model.Image
import com.schoolkiller.BuildConfig
import java.io.ByteArrayOutputStream
import java.util.Locale


class CloudVisionApi {

    private val CLOUD_VISION_API_KEY: String = BuildConfig.google_cloud_api_key
    private val MAX_DIMENSION = 1200

    fun getAnnotatedImage(bitMap: Bitmap?): Vision.Images.Annotate? {
        val httpTransport = AndroidHttp.newCompatibleTransport()
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

        val requestInitializer: VisionRequestInitializer = getVisionRequestInitializer()

        val builder = Vision.Builder(httpTransport, jsonFactory, null)
        builder.setVisionRequestInitializer(requestInitializer)

        val vision = builder.build()

        val batchAnnotateImagesRequest = BatchAnnotateImagesRequest()
        batchAnnotateImagesRequest.setRequests(getAnnotateImagesRequest(bitMap))

        val annotateRequest = vision.images().annotate(batchAnnotateImagesRequest)

        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true)
        println("created Cloud Vision request object, sending request")

        val response: BatchAnnotateImagesResponse = annotateRequest.execute()
        convertResponseToString(response)

        return annotateRequest
    }

    private fun getVisionRequestInitializer(): VisionRequestInitializer {
        return object : VisionRequestInitializer(CLOUD_VISION_API_KEY) {
            /**
             * We override this so we can inject important identifying fields into the HTTP
             * headers. This enables use of a restricted cloud platform API key.
             */
            /*
            @Throws(IOException::class)
            override fun initializeVisionRequest(visionRequest: VisionRequest<*>) {
                super.initializeVisionRequest(visionRequest)

                val packageName: String = getPackageName()
                visionRequest.requestHeaders[ANDROID_PACKAGE_HEADER] = packageName

                val sig: String =
                    PackageManagerUtils.getSignature(getPackageManager(), packageName)

                visionRequest.requestHeaders[ANDROID_CERT_HEADER] = sig
            }
             */
        }
    }

    private fun getAnnotateImagesRequest(bitMap: Bitmap?): List<AnnotateImageRequest?> {
        val request = object : ArrayList<AnnotateImageRequest?>() {
            init {
                val annotateImageRequest = AnnotateImageRequest()

                // Add the image
                val base64EncodedImage = Image()
                // Convert the bitmap to a JPEG
                // Just in case it's a format that Android understands but Cloud Vision
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitMap?.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()

                // Base64 encode the JPEG
                base64EncodedImage.encodeContent(imageBytes)
                annotateImageRequest.setImage(base64EncodedImage)

                // add the features we want
                annotateImageRequest.setFeatures(object : ArrayList<Feature?>() {
                    init {
                        val labelDetection = Feature()
                        labelDetection.setType("TEXT_DETECTION")
                        //labelDetection.setMaxResults(MAX_LABEL_RESULTS)
                        add(labelDetection)
                    }
                })

                // Add the list of one thing to the request
                add(annotateImageRequest)
            }
        }
        return request
    }

    private fun convertResponseToString(response: BatchAnnotateImagesResponse): String {
        val message: StringBuilder = StringBuilder("I found these things:\n\n")

        val labels: List<EntityAnnotation> = response.responses[0].labelAnnotations
        for (label in labels) {
            message.append(
                String.format(
                    Locale.US,
                    "%.3f: %s",
                    label.score,
                    label.description
                )
            );
            message.append("\n");
        }

        return message.toString();
    }


}