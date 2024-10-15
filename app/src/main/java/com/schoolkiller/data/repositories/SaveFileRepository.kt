package com.schoolkiller.data.repositories

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class SaveFileRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    var getUri: Uri? = null

    suspend fun saveImage(bitmap: Bitmap): Uri? {
        withContext(Dispatchers.IO) {

            val resolver = context.contentResolver

            val imageCollection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )

            val timeInMillis = System.currentTimeMillis()

            val imageContentValues = ContentValues().apply {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/SchoolKiller"
                )
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    "${timeInMillis}_image" + ".jpg"
                )
                put(
                    MediaStore.MediaColumns.MIME_TYPE, "image/jpg"
                )
                put(
                    MediaStore.MediaColumns.DATE_TAKEN, timeInMillis
                )
                put(
                    MediaStore.MediaColumns.IS_PENDING, 1
                )
            }

            val imageMediaStoreUri = resolver.insert(
                imageCollection, imageContentValues
            )

            imageMediaStoreUri?.let { uri ->
                try {

                    resolver.openOutputStream(uri)?.let { outputStream ->
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG, 100, outputStream
                        )
                    }

                    imageContentValues.clear()

                    imageContentValues.put(
                        MediaStore.MediaColumns.IS_PENDING, 0
                    )

                    resolver.update(
                        uri, imageContentValues, null, null
                    )

                    getUri = uri

                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(uri, null, null)
                    return@withContext null // Return null if an error occurs
                }
            } ?: run {
                return@withContext null // Return null if insertion fails
            }
        }
        return getUri
    }

    fun getCameraSavedImageUri() : Uri? {
        return getUri
    }
}


