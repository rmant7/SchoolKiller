package com.schoolkiller.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class ImageUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun convertUriToByteArray(imageUri: Uri): ByteArray {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun convertToBase64(selectedUri: Uri): String {
        val bitmap: Bitmap =
            scaleBitmapDown(
                MediaStore.Images.Media.getBitmap(context.contentResolver, selectedUri),
                200
            )

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val byteArray = outputStream.toByteArray()

        /* val base64EncodedImage = Image.Builder().base64Data(
             android.util.Base64.encodeToString(
             byteArray, android.util.Base64.DEFAULT
         )
         ).build()*/
        //val base64EncodedImage = base64EncodedImage.encodeContent(byteArray)

        val encodedString: String = Base64.encodeToString(
            byteArray, Base64.DEFAULT
        )
        println(encodedString)
        return encodedString
        // return base64EncodedImage
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {

        val originalWidth = bitmap.getWidth()
        val originalHeight = bitmap.getHeight()
        val resizedWidth: Int
        val resizedHeight: Int

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension
            resizedWidth = resizedHeight * originalWidth / originalHeight
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = resizedWidth * originalHeight / originalWidth
        } else {
            resizedHeight = maxDimension
            resizedWidth = maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
}