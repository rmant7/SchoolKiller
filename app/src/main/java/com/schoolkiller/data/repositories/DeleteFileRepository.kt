package com.schoolkiller.data.repositories

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.schoolkiller.data.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


@ViewModelScoped
class DeleteFileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {


     fun deleteImageFromStorage(
        activity: Activity,
        imageUri: Uri
    ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For Android 10 and above, use createDeleteRequest
                val pendingIntent = MediaStore.createDeleteRequest(
                    context.contentResolver,
                    listOf(imageUri)
                )
//            val activity = context as Activity
                activity.startIntentSenderForResult(
                    pendingIntent.intentSender,
                    Constants.DELETE_REQUEST_CODE,
                    null,
                    0,
                    0,
                    0,
                    null
                )

            } else {
                // For older Android versions, use contentResolver.delete
                imageUri?.let {
                    context.contentResolver.delete(
                        it,
                        null,
                        null
                    )
                }
            }
    }


    fun cleanInvalidImages(
        activity: Activity,
        invalidUris: List<Uri?>
    ) {
        invalidUris.forEach { uri ->
            var count = 0

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
//                 MediaStore.Images.Media.RELATIVE_PATH  // uncomment if you want to be asked to delete valid images also
            )

            val selection = "${MediaStore.Images.Media._ID} = ?"


            val selectionArgs = arrayOf(uri?.let { ContentUris.parseId(it).toString() })

            try {

                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )

                cursor?.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    count = it.count
                    Timber.d("Found $count images in SchoolKiller_Images folder")


                    while (it.moveToNext()) {
                        val imageId = it.getLong(idColumn)
                        val imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            imageId
                        )
                        Timber.d("Checking image with URI: $imageUri")

                        try {
                            val rowsDeleted =
                                context.contentResolver.delete(imageUri, null, null)
                            if (rowsDeleted > 0) {
                                Timber.d(
                                    "Successfully deleted image with URI: $imageUri"
                                )
                            } else {
                                Timber.w("Failed to delete image with URI: $imageUri")
                            }
                        } catch (e: RecoverableSecurityException) {
                            Timber.e(
                                "RecoverableSecurityException for URI: $imageUri", e
                            )
                            val intentSender = e.userAction.actionIntent.intentSender
//                            val activity = context as Activity
                            activity.startIntentSenderForResult(
                                intentSender,
                                Constants.DELETE_REQUEST_CODE,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        } catch (e: Exception) {
                            Timber.e("Error deleting image with URI: $imageUri", e)
                        }

                    }
                }
            } catch (e: Exception) {
                Timber.e("Error querying MediaStore", e)
            }
        }
    }


    fun getInvalidImageUris(): List<Uri> {
        val inValidUris = mutableListOf<Uri>()
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val imageId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val uri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )
                if (!checkUriValidity(uri)) {
                    inValidUris.add(uri)
                }
            }
        }
        return inValidUris

    }

    fun checkUriValidity(uri: Uri): Boolean {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.close()
            true // URI is valid
        } catch (e: FileNotFoundException) {
            false // File not found
        } catch (e: IOException) {
            false // Other IO exceptions
        }
    }

}


// maybe needed
/*
    fun deleteGhostImagePathSpecific(
        context: Context,
        activity: Activity,
        checkedUris: List<Uri?> = emptyList()
    ) {
        checkedUris.forEach { checkedUri ->
            var count = 0

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.RELATIVE_PATH
            )

            val selection =
                if (checkedUri != null) {
                    "${MediaStore.Images.Media._ID} = ?"
                } else {
                    "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
                }
            val selectionArgs = if (checkedUri != null) {
                arrayOf(ContentUris.parseId(checkedUri).toString())
            } else {
                arrayOf("%SchoolKiller%") // choose which path inside Pictures directory
            }

            try {

                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )

                cursor?.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    count = it.count
                    Timber.d("Found $count images in SchoolKiller_Images folder")


                    while (it.moveToNext()) {
                        val imageId = it.getLong(idColumn)
                        val imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            imageId
                        )
                        Timber.d("Checking image with URI: $imageUri")

                        try {
                            val rowsDeleted =
                                context.contentResolver.delete(imageUri, null, null)
                            if (rowsDeleted > 0) {
                                Timber.d(
                                    "Successfully deleted image with URI: $imageUri"
                                )
                            } else {
                                Timber.w("Failed to delete image with URI: $imageUri")
                            }
                        } catch (e: RecoverableSecurityException) {
                            Timber.e(
                                "RecoverableSecurityException for URI: $imageUri", e
                            )
                            val intentSender = e.userAction.actionIntent.intentSender
                            activity.startIntentSenderForResult(
                                intentSender,
                                Constants.DELETE_REQUEST_CODE,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        } catch (e: Exception) {
                            Timber.e("Error deleting image with URI: $imageUri", e)
                        }

                    }
                }
            } catch (e: Exception) {
                Timber.e("Error querying MediaStore", e)
            }
        }

    }

 */
